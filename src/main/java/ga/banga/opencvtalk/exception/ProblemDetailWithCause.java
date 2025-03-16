package ga.banga.opencvtalk.exception;

import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant un détail de problème avec sa cause.
 * Étend la classe ProblemDetail de Spring.
 */
public class ProblemDetailWithCause extends org.springframework.http.ProblemDetail {

    private String cause;
    private Map<String, Object> properties = new HashMap<>();

    public ProblemDetailWithCause() {
        super();
    }

    public ProblemDetailWithCause(HttpStatusCode status) {
        super(status.value());
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}

