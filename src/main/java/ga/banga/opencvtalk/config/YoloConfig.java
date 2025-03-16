package ga.banga.opencvtalk.config;

import org.bytedeco.opencv.opencv_dnn.Net;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_dnn.*;

@Configuration
public class YoloConfig {

    // Dimensions d'entrée du réseau YOLO
    public static final int INPUT_WIDTH = 416;
    public static final int INPUT_HEIGHT = 416;

    // Seuils de détection
    public static final float CONFIDENCE_THRESHOLD = 0.3f;
    public static final float NMS_THRESHOLD = 0.4f;

    // Paramètres d'extraction de caractéristiques
    public static final int EMBEDDING_SIZE = 1024;

    // Paramètres de sécurité pour les boîtes englobantes
    public static final double MIN_BOX_SIZE = 10.0;  // Taille minimale en pixels
    public static final double MAX_BOX_RATIO = 0.9;  // Ratio maximal par rapport à l'image

    @Bean
    public Net yoloNetwork() throws IOException {
        // Charger le modèle YOLO
        ClassPathResource configResource = new ClassPathResource("models/yolo/yolov4.cfg");
        ClassPathResource weightsResource = new ClassPathResource("models/yolo/yolov4.weights");

        Net network = readNetFromDarknet(
                configResource.getFile().getAbsolutePath(),
                weightsResource.getFile().getAbsolutePath()
        );

        // Configuration GPU/CPU avec gestion des erreurs
        try {
            network.setPreferableBackend(DNN_BACKEND_CUDA);
            network.setPreferableTarget(DNN_TARGET_CUDA);
        } catch (Exception e) {
            network.setPreferableBackend(DNN_BACKEND_OPENCV);
            network.setPreferableTarget(DNN_TARGET_CPU);
        }

        return network;
    }

    @Bean
    public Net featureExtractor() throws IOException {
        return yoloNetwork();
    }

    @Bean
    public List<String> yoloClasses() throws IOException {
        List<String> classes = new ArrayList<>();
        ClassPathResource namesResource = new ClassPathResource("models/yolo/coco.names");
        String[] classNames = new String(namesResource.getInputStream().readAllBytes()).split("\n");
        for (String className : classNames) {
            classes.add(className.trim());
        }
        return classes;
    }

    @Bean
    public YoloProperties yoloProperties() {
        return new YoloProperties(
                INPUT_WIDTH,
                INPUT_HEIGHT,
                CONFIDENCE_THRESHOLD,
                NMS_THRESHOLD,
                EMBEDDING_SIZE,
                MIN_BOX_SIZE,
                MAX_BOX_RATIO
        );
    }
}