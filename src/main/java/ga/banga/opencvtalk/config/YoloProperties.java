package ga.banga.opencvtalk.config;

import java.util.Objects;

public class YoloProperties {
    private final int inputWidth;
    private final int inputHeight;
    private final float confidenceThreshold;
    private final float nmsThreshold;
    private final int embeddingSize;
    private final double minBoxSize;
    private final double maxBoxRatio;

    public YoloProperties(int inputWidth, int inputHeight, float confidenceThreshold,
                          float nmsThreshold, int embeddingSize, double minBoxSize, double maxBoxRatio) {
        this.inputWidth = inputWidth;
        this.inputHeight = inputHeight;
        this.confidenceThreshold = confidenceThreshold;
        this.nmsThreshold = nmsThreshold;
        this.embeddingSize = embeddingSize;
        this.minBoxSize = minBoxSize;
        this.maxBoxRatio = maxBoxRatio;
    }

    public int getInputWidth() {
        return inputWidth;
    }

    public int getInputHeight() {
        return inputHeight;
    }

    public float getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public float getNmsThreshold() {
        return nmsThreshold;
    }

    public int getEmbeddingSize() {
        return embeddingSize;
    }

    public double getMinBoxSize() {
        return minBoxSize;
    }

    public double getMaxBoxRatio() {
        return maxBoxRatio;
    }

    /**
     * Valide si une boîte englobante est valide par rapport aux dimensions de l'image
     *
     * @param x           coordonnée x du coin supérieur gauche
     * @param y           coordonnée y du coin supérieur gauche
     * @param width       largeur de la boîte
     * @param height      hauteur de la boîte
     * @param imageWidth  largeur de l'image
     * @param imageHeight hauteur de l'image
     * @return true si la boîte est valide, false sinon
     */
    public boolean isValidBoundingBox(int x, int y, int width, int height, int imageWidth, int imageHeight) {
        // Vérifier les coordonnées négatives
        if (x < 0 || y < 0) return false;

        // Vérifier les dimensions minimales
        if (width < minBoxSize || height < minBoxSize) return false;

        // Vérifier que la boîte ne dépasse pas les limites de l'image
        if (x + width > imageWidth || y + height > imageHeight) return false;

        // Vérifier le ratio maximal
        double boxRatio = Math.max(
                (double) width / imageWidth,
                (double) height / imageHeight
        );
        return boxRatio <= maxBoxRatio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YoloProperties that = (YoloProperties) o;
        return inputWidth == that.inputWidth &&
                inputHeight == that.inputHeight &&
                Float.compare(confidenceThreshold, that.confidenceThreshold) == 0 &&
                Float.compare(nmsThreshold, that.nmsThreshold) == 0 &&
                embeddingSize == that.embeddingSize &&
                Double.compare(minBoxSize, that.minBoxSize) == 0 &&
                Double.compare(maxBoxRatio, that.maxBoxRatio) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputWidth, inputHeight, confidenceThreshold,
                nmsThreshold, embeddingSize, minBoxSize, maxBoxRatio);
    }

    @Override
    public String toString() {
        return "YoloProperties{" +
                "inputWidth=" + inputWidth +
                ", inputHeight=" + inputHeight +
                ", confidenceThreshold=" + confidenceThreshold +
                ", nmsThreshold=" + nmsThreshold +
                ", embeddingSize=" + embeddingSize +
                ", minBoxSize=" + minBoxSize +
                ", maxBoxRatio=" + maxBoxRatio +
                '}';
    }
} 