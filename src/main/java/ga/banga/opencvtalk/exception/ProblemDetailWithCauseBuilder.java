package ga.banga.opencvtalk.exception;

import java.net.URI;
import java.util.Map;

/**
 * Builder pour créer des instances de ProblemDetailWithCause.
 * Utilise le pattern Builder pour une construction fluide.
 */
public class ProblemDetailWithCauseBuilder {

    private final ProblemDetailWithCause problemDetailWithCause;

    private ProblemDetailWithCauseBuilder() {
        this.problemDetailWithCause = new ProblemDetailWithCause();
    }

    public static ProblemDetailWithCauseBuilder instance() {
        return new ProblemDetailWithCauseBuilder();
    }

    public ProblemDetailWithCauseBuilder withStatus(int status) {
        this.problemDetailWithCause.setStatus(status);
        return this;
    }

    public ProblemDetailWithCauseBuilder withType(URI type) {
        this.problemDetailWithCause.setType(type);
        return this;
    }

    public ProblemDetailWithCauseBuilder withTitle(String title) {
        this.problemDetailWithCause.setTitle(title);
        return this;
    }

    public ProblemDetailWithCauseBuilder withDetail(String detail) {
        this.problemDetailWithCause.setDetail(detail);
        return this;
    }

    public ProblemDetailWithCauseBuilder withInstance(URI instance) {
        this.problemDetailWithCause.setInstance(instance);
        return this;
    }

    public ProblemDetailWithCauseBuilder withCause(String cause) {
        this.problemDetailWithCause.setCause(cause);
        return this;
    }

    public ProblemDetailWithCauseBuilder withProperty(String key, Object value) {
        Map<String, Object> properties = this.problemDetailWithCause.getProperties();
        properties.put(key, value);
        return this;
    }

    public ProblemDetailWithCause build() {
        return this.problemDetailWithCause;
    }
}
