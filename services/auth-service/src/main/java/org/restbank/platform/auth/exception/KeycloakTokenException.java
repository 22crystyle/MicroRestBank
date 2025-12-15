package org.restbank.platform.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.util.Map;

@Getter
public class KeycloakTokenException extends RuntimeException {
    private final HttpStatusCode status;
    private final Map<String, String> details;
    private final String rawBody;

    public KeycloakTokenException(HttpStatusCode status, Map<String, String> details, String rawBody) {
        super("Keycloak token error: " + rawBody);
        this.status = status;
        this.details = details;
        this.rawBody = rawBody;
    }
}
