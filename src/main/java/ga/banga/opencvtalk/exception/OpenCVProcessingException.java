package ga.banga.opencvtalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class OpenCVProcessingException extends RuntimeException {

    public OpenCVProcessingException(String message) {
        super(message);
    }

    public OpenCVProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
} 