package ga.banga.opencvtalk.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "object_detections")
public class ObjectDetection {
    public static final int EMBEDDING_DIMENSION = 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String objectClass;
    private float confidence;
    private String imagePath;

    @Column(name = "embedding", columnDefinition = "vector(1024)") // Selon la dimension des caractéristiques
    private float[] embedding;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
} 