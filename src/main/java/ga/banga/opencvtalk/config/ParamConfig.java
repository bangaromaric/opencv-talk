package ga.banga.opencvtalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ParamConfig {
    private float threshold;
    private float duplicateThreshold;
    private String photoDirectory;


    public float getDuplicateThreshold() {
        return duplicateThreshold;
    }

    public void setDuplicateThreshold(float duplicateThreshold) {
        this.duplicateThreshold = duplicateThreshold;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public String getPhotoDirectory() {
        return photoDirectory;
    }

    public void setPhotoDirectory(String photoDirectory) {
        this.photoDirectory = photoDirectory;
    }
}
