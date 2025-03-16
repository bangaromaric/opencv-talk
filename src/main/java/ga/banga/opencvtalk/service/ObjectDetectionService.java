package ga.banga.opencvtalk.service;

import ga.banga.opencvtalk.model.ObjectDetection;
import ga.banga.opencvtalk.repository.ObjectDetectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_dnn.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@Service
public class ObjectDetectionService {

    private static final int INPUT_WIDTH = 416;
    private static final int INPUT_HEIGHT = 416;
    private static final float CONFIDENCE_THRESHOLD = 0.3f;
    private static final float NMS_THRESHOLD = 0.4f;
    private static final int EMBEDDING_SIZE = 1024;
    private final ObjectDetectionRepository repository;
    private final FileStorageService fileStorageService;
    private final Net network;
    private final Net featureExtractor; // Extracteur de features dédié
    private final List<String> classes;
    private final DataSource dataSource;
    @PersistenceContext
    private EntityManager entityManager;

    public ObjectDetectionService(ObjectDetectionRepository repository, FileStorageService fileStorageService, DataSource dataSource) throws IOException {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.dataSource = dataSource;

        // Charger les noms des classes
        classes = new ArrayList<>();
        ClassPathResource namesResource = new ClassPathResource("models/yolo/coco.names");
        String[] classNames = new String(namesResource.getInputStream().readAllBytes()).split("\n");
        for (String className : classNames) {
            classes.add(className.trim());
        }

        // Charger le modèle YOLO pour la détection
        ClassPathResource configResource = new ClassPathResource("models/yolo/yolov4.cfg");
        ClassPathResource weightsResource = new ClassPathResource("models/yolo/yolov4.weights");

        network = readNetFromDarknet(
                configResource.getFile().getAbsolutePath(),
                weightsResource.getFile().getAbsolutePath()
        );

        // Configurer l'extracteur de features (utiliser le backbone de YOLOv4)
        featureExtractor = readNetFromDarknet(
                configResource.getFile().getAbsolutePath(),
                weightsResource.getFile().getAbsolutePath()
        );

        // Lister toutes les couches disponibles
        StringVector layerNames = featureExtractor.getLayerNames();
        System.out.println("Couches disponibles dans le modèle:");
        for (int i = 0; i < layerNames.size(); i++) {
            System.out.println(layerNames.get(i).getString());
        }

        try {
            // Utiliser CUDA si disponible
            network.setPreferableBackend(DNN_BACKEND_CUDA);
            network.setPreferableTarget(DNN_TARGET_CUDA);
            featureExtractor.setPreferableBackend(DNN_BACKEND_CUDA);
            featureExtractor.setPreferableTarget(DNN_TARGET_CUDA);
        } catch (Exception e) {
            // Fallback sur CPU
            network.setPreferableBackend(DNN_BACKEND_OPENCV);
            network.setPreferableTarget(DNN_TARGET_CPU);
            featureExtractor.setPreferableBackend(DNN_BACKEND_OPENCV);
            featureExtractor.setPreferableTarget(DNN_TARGET_CPU);
        }
    }

    private float[] extractFeatures(Mat roi) {
        // Prétraitement
        Mat blob = blobFromImage(roi, 1 / 255.0, new Size(INPUT_WIDTH, INPUT_HEIGHT),
                new Scalar(0, 0, 0, 0), true, false, CV_32F);

        featureExtractor.setInput(blob);

        // Utiliser la couche conv_105 pour l'extraction des features
        Mat features = featureExtractor.forward("conv_105");

        // Afficher les informations détaillées sur les dimensions
        System.out.println("Dimensions de la matrice: " + features.dims());
        System.out.println("Taille par dimension:");
        for (int i = 0; i < features.dims(); i++) {
            System.out.println("Dimension " + i + ": " + features.size(i));
        }
        System.out.println("Nombre de canaux: " + features.channels());

        // Calculer la taille totale
        long totalSize = features.total() * features.channels();
        System.out.println("Taille totale: " + totalSize);

        // Redimensionner à EMBEDDING_SIZE
        float[] embedding = new float[EMBEDDING_SIZE];
        FloatPointer featureData = new FloatPointer(features.ptr());

        // Moyenner sur les dimensions spatiales (13x13) pour obtenir 512 features
        float[] intermediateFeatures = new float[512];

        // Moyenner sur les dimensions spatiales
        for (int c = 0; c < 512; c++) {
            float sum = 0;
            for (int h = 0; h < 13; h++) {
                for (int w = 0; w < 13; w++) {
                    long index = (h * 13 * 512) + (w * 512) + c;
                    float value = featureData.get(index);
                    if (!Float.isNaN(value) && !Float.isInfinite(value)) {
                        sum += value;
                    }
                }
            }
            intermediateFeatures[c] = sum / (13 * 13);
        }

        // Puis étendre à 1024 dimensions par interpolation
        for (int i = 0; i < EMBEDDING_SIZE; i++) {
            float pos = i * 512.0f / EMBEDDING_SIZE;
            int index = (int) pos;
            float frac = pos - index;

            if (index >= 511) {
                embedding[i] = intermediateFeatures[511];
            } else {
                embedding[i] = (1 - frac) * intermediateFeatures[index] +
                        frac * intermediateFeatures[index + 1];
            }
        }

        // Normalisation L2 avec vérification
        double squaredSum = 0.0;
        for (float v : embedding) {
            squaredSum += v * v;
        }

        // Éviter la division par zéro et vérifier la validité de la norme
        double norm = Math.sqrt(squaredSum);
        if (norm > 1e-10) {
            float normFloat = (float) norm;
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= normFloat;

                // Vérification finale pour s'assurer qu'il n'y a pas de NaN
                if (Float.isNaN(embedding[i]) || Float.isInfinite(embedding[i])) {
                    embedding[i] = 0.0f;
                }
            }
        } else {
            // Si la norme est trop petite, initialiser un vecteur unitaire
            embedding[0] = 1.0f;
            for (int i = 1; i < embedding.length; i++) {
                embedding[i] = 0.0f;
            }
        }

        // Vérification finale de la taille et des valeurs
        if (embedding.length != EMBEDDING_SIZE) {
            throw new IllegalStateException("La taille de l'embedding (" + embedding.length +
                    ") ne correspond pas à la taille attendue (" + EMBEDDING_SIZE + ")");
        }

        for (int i = 0; i < embedding.length; i++) {
            if (Float.isNaN(embedding[i]) || Float.isInfinite(embedding[i])) {
                throw new IllegalStateException("Valeur invalide détectée dans l'embedding à l'index " + i);
            }
        }

        return embedding;
    }

    public ObjectDetection detectAndSave(MultipartFile file) throws IOException {
        UUID objectId = UUID.randomUUID();
        String fileName = fileStorageService.storePhoto(file, objectId);
        String fullPath = fileStorageService.loadPhotoAsResource(fileName).getFile().getAbsolutePath();

        System.out.println("Début de la détection pour l'image: " + fileName);

        // Charger et prétraiter l'image
        Mat image = imread(fullPath);
        if (image.empty()) {
            throw new IOException("Impossible de charger l'image: " + fullPath);
        }

        System.out.println("Dimensions de l'image: " + image.cols() + "x" + image.rows());

        Mat inputBlob = blobFromImage(image, 1 / 255.0, new Size(INPUT_WIDTH, INPUT_HEIGHT),
                new Scalar(0, 0, 0, 0), true, false, CV_32F);

        try {
            // Détection avec YOLO
            network.setInput(inputBlob);
            MatVector outs = new MatVector();
            StringVector outNames = network.getUnconnectedOutLayersNames();
            network.forward(outs, outNames);

            System.out.println("Nombre de sorties du réseau: " + outs.size());

            // Post-traitement
            List<Rect> boxesList = new ArrayList<>();
            List<Float> confidencesList = new ArrayList<>();
            List<Integer> classIdsList = new ArrayList<>();

            // Analyser les sorties
            for (int i = 0; i < outs.size(); ++i) {
                Mat output = outs.get(i);
                FloatPointer data = new FloatPointer(output.ptr());

                System.out.println("Analyse de la sortie " + i + " avec " + output.rows() + " détections");

                for (int j = 0; j < output.rows(); ++j) {
                    Mat scores = output.row(j).colRange(5, output.cols());
                    Point classIdPoint = new Point();
                    DoublePointer confidence = new DoublePointer(1);
                    minMaxLoc(scores, null, confidence, null, classIdPoint, null);

                    if (confidence.get() > CONFIDENCE_THRESHOLD) {
                        float centerX = data.get((long) j * output.cols()) * image.cols();
                        float centerY = data.get((long) j * output.cols() + 1) * image.rows();
                        float width = data.get((long) j * output.cols() + 2) * image.cols();
                        float height = data.get((long) j * output.cols() + 3) * image.rows();

                        System.out.println("Détection trouvée - Classe: " + classes.get(classIdPoint.x()) +
                                ", Confiance: " + String.format("%.2f", confidence.get()));

                        boxesList.add(new Rect(
                                (int) (centerX - width / 2),
                                (int) (centerY - height / 2),
                                (int) width,
                                (int) height
                        ));
                        confidencesList.add((float) confidence.get());
                        classIdsList.add(classIdPoint.x());
                    }
                }
            }

            System.out.println("Nombre total de détections avant NMS: " + boxesList.size());

            if (boxesList.isEmpty()) {
                // Essayer avec un seuil plus bas si aucune détection
                float lowerThreshold = 0.1f;
                System.out.println("Aucune détection avec le seuil standard. Tentative avec un seuil plus bas: " + lowerThreshold);

                // Réinitialiser les listes
                boxesList.clear();
                confidencesList.clear();
                classIdsList.clear();

                // Refaire l'analyse avec un seuil plus bas
                for (int i = 0; i < outs.size(); ++i) {
                    Mat output = outs.get(i);
                    FloatPointer data = new FloatPointer(output.ptr());

                    for (int j = 0; j < output.rows(); ++j) {
                        Mat scores = output.row(j).colRange(5, output.cols());
                        Point classIdPoint = new Point();
                        DoublePointer confidence = new DoublePointer(1);
                        minMaxLoc(scores, null, confidence, null, classIdPoint, null);

                        if (confidence.get() > lowerThreshold) {
                            float centerX = data.get((long) j * output.cols()) * image.cols();
                            float centerY = data.get((long) j * output.cols() + 1) * image.rows();
                            float width = data.get((long) j * output.cols() + 2) * image.cols();
                            float height = data.get((long) j * output.cols() + 3) * image.rows();

                            System.out.println("Détection trouvée (seuil bas) - Classe: " + classes.get(classIdPoint.x()) +
                                    ", Confiance: " + String.format("%.2f", confidence.get()));

                            boxesList.add(new Rect(
                                    (int) (centerX - width / 2),
                                    (int) (centerY - height / 2),
                                    (int) width,
                                    (int) height
                            ));
                            confidencesList.add((float) confidence.get());
                            classIdsList.add(classIdPoint.x());
                        }
                    }
                }
            }

            if (boxesList.isEmpty()) {
                throw new RuntimeException("Aucun objet détecté avec une confiance suffisante. Essayez avec une image plus claire ou un autre angle.");
            }

            // Préparer les données pour NMS
            RectVector boxes = new RectVector(boxesList.size());
            FloatPointer confidences = new FloatPointer(confidencesList.size());
            IntPointer classIds = new IntPointer(classIdsList.size());

            for (int i = 0; i < boxesList.size(); i++) {
                boxes.put(i, boxesList.get(i));
                confidences.put(i, confidencesList.get(i));
                classIds.put(i, classIdsList.get(i));
            }

            // Non-maximum suppression
            IntPointer indices = new IntPointer(confidences.capacity());
            NMSBoxes(boxes, confidences, CONFIDENCE_THRESHOLD, NMS_THRESHOLD, indices);

            // Prendre la détection avec la plus haute confiance
            if (indices.capacity() > 0) {
                int idx = indices.get(0);
                String detectedClass = classes.get(classIds.get(idx));
                float confidence = confidences.get(idx);

                // Extraire la ROI
                Rect box = boxes.get(idx);
                Mat roi = new Mat(image, box);

                // Extraire les features avec le modèle dédié
                float[] embedding = extractFeatures(roi);

                // Log pour vérifier la taille avant la création de l'objet
                System.out.println("Taille du vecteur avant création de l'objet: " + embedding.length);

                // Vérifier quelques valeurs du vecteur
                System.out.println("Échantillon des 5 premières valeurs du vecteur:");
                for (int i = 0; i < Math.min(5, embedding.length); i++) {
                    System.out.println("embedding[" + i + "] = " + embedding[i]);
                }

                // Créer et sauvegarder l'objet détecté
                ObjectDetection detection = new ObjectDetection();
                detection.setImagePath(fileName);
                detection.setEmbedding(embedding);
                detection.setObjectClass(detectedClass);
                detection.setConfidence(confidence);

                // Log pour vérifier la taille après la création de l'objet
                System.out.println("Taille du vecteur dans l'objet: " + detection.getEmbedding().length);

                // Sauvegarder et récupérer l'objet
                ObjectDetection savedDetection = repository.save(detection);

                // Log pour vérifier la taille après la sauvegarde
                System.out.println("Taille du vecteur après sauvegarde: " + savedDetection.getEmbedding().length);

                return savedDetection;
            }

            throw new RuntimeException("Aucun objet détecté avec une confiance suffisante");
        } finally {
            // Libérer la mémoire
            image.release();
            inputBlob.release();
        }
    }

    @Transactional(readOnly = true)
    public List<ObjectDetection> findSimilarObjects(float[] embedding, int limit) {
        if (embedding == null) {
            throw new IllegalArgumentException("Le vecteur d'embedding ne peut pas être null");
        }

        if (embedding.length != EMBEDDING_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Le vecteur d'embedding doit avoir une taille de %d, mais a une taille de %d",
                            EMBEDDING_SIZE, embedding.length)
            );
        }

        // Normalisation L2 du vecteur de recherche
        double squaredSum = 0.0;
        for (float v : embedding) {
            squaredSum += v * v;
        }
        double norm = Math.sqrt(squaredSum);
        if (norm > 1e-10) {
            float normFloat = (float) norm;
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= normFloat;
            }
        }

        System.out.println("Taille du vecteur de recherche: " + embedding.length);

        List<ObjectDetection> results = new ArrayList<>();
        String sql = """
                WITH search_vector AS (
                    SELECT ?::vector as vec
                ),
                source_object AS (
                    SELECT id, object_class
                    FROM object_detections
                    WHERE embedding <=> (SELECT vec FROM search_vector) < 0.01
                    ORDER BY embedding <=> (SELECT vec FROM search_vector) ASC
                    LIMIT 1
                )
                SELECT od.id, od.object_class, od.confidence, od.image_path,
                       od.embedding <=> (SELECT vec FROM search_vector) AS distance
                FROM object_detections od
                WHERE od.object_class = (SELECT object_class FROM source_object)
                AND od.id != (SELECT id FROM source_object)
                AND od.embedding <=> (SELECT vec FROM search_vector) < 0.8
                ORDER BY distance ASC
                LIMIT ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Construire la représentation du vecteur pour pgvector
            StringBuilder vectorStr = new StringBuilder("[");
            for (int i = 0; i < embedding.length; i++) {
                if (i > 0) {
                    vectorStr.append(",");
                }
                vectorStr.append(String.format(java.util.Locale.US, "%.8f", embedding[i]));
            }
            vectorStr.append("]");

            System.out.println("Recherche d'objets similaires avec un vecteur de taille: " + embedding.length);

            stmt.setString(1, vectorStr.toString());
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ObjectDetection detection = new ObjectDetection();
                    detection.setId(UUID.fromString(rs.getString("id")));
                    detection.setObjectClass(rs.getString("object_class"));
                    detection.setConfidence(rs.getFloat("confidence"));
                    detection.setImagePath(rs.getString("image_path"));
                    float distance = rs.getFloat("distance");
                    System.out.println("Objet trouvé: " + detection.getObjectClass() +
                            " avec distance: " + distance +
                            " (id: " + detection.getId() + ")");
                    results.add(detection);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL complète: " + e);
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche d'objets similaires: " + e.getMessage(), e);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public ObjectDetection findById(UUID id) {
        String sql = """
                SELECT id, object_class, confidence, image_path
                FROM object_detections
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ObjectDetection detection = new ObjectDetection();
                    detection.setId(id);
                    detection.setObjectClass(rs.getString("object_class"));
                    detection.setConfidence(rs.getFloat("confidence"));
                    detection.setImagePath(rs.getString("image_path"));
                    return detection;
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL complète lors de la recherche par ID: " + e);
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche de l'objet: " + e.getMessage(), e);
        }
    }
} 
