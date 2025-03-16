package ga.banga.opencvtalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestAlertException extends ResponseStatusException {

    public BadRequestAlertException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public BadRequestAlertException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }

    // Optionnel : Pour des messages plus structurés avec entité et clé d'erreur
    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        super(HttpStatus.BAD_REQUEST, defaultMessage);
        // Stocker les informations supplémentaires si nécessaire
    }

}