package ga.banga.opencvtalk.exception;

import java.util.List;

public class InvalidPhotoException extends RuntimeException {
    private final List<String> validationErrors;

    public InvalidPhotoException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}