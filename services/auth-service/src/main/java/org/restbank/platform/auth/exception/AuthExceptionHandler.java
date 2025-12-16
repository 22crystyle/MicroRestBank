package org.restbank.platform.auth.exception;

import org.restbank.libs.api.dto.response.ValidationErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Authentication Service.
 *
 * <p>This class captures and handles exceptions thrown by the controllers,
 * providing a consistent and structured error response.
 */
@RestControllerAdvice
public class AuthExceptionHandler {
    private static final String MESSAGE = "message";

    private final String applicationName;

    public AuthExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Handles {@link KeycloakTokenException} and returns an appropriate error response.
     *
     * @param ex The caught {@link KeycloakTokenException}.
     * @return A {@link Mono} containing a {@link ResponseEntity} with the error details.
     */
    @ExceptionHandler(KeycloakTokenException.class)
    public Mono<ResponseEntity<Object>> handleKeycloakTokenException(KeycloakTokenException ex) {
        if (ex.getStatus() == HttpStatus.BAD_REQUEST || ex.getStatus() == HttpStatus.CONFLICT || ex.getStatus() == HttpStatus.UNAUTHORIZED) {
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                    applicationName,
                    String.valueOf(ex.getStatus().value()),
                    "Keycloak error",
                    ex.getDetails()
            );
            return Mono.just(ResponseEntity.status(ex.getStatus()).body(errorResponse));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("source", "keycloak");
        body.put("status", ex.getStatus().value());
        body.putAll(ex.getDetails());
        return Mono.just(ResponseEntity
                .status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    /**
     * Handles {@link UserCreationException} and returns a 500 Internal Server Error response.
     *
     * @param ex The caught {@link UserCreationException}.
     * @return A {@link Mono} containing a {@link ResponseEntity} with the error message.
     */
    @ExceptionHandler(UserCreationException.class)
    public Mono<ResponseEntity<Object>> handleUserCreationException(UserCreationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    /**
     * Handles {@link AdminTokenException} and returns a 500 Internal Server Error response.
     *
     * @param ex The caught {@link AdminTokenException}.
     * @return A {@link Mono} containing a {@link ResponseEntity} with the error message.
     */
    @ExceptionHandler(AdminTokenException.class)
    public Mono<ResponseEntity<Object>> handleAdminTokenException(AdminTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(MESSAGE, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    /**
     * Handles {@link WebExchangeBindException} for validation errors and returns a 400 Bad Request response.
     *
     * @param ex The caught {@link WebExchangeBindException}.
     * @return A {@link Mono} containing a {@link ResponseEntity} with validation error details.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Object>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));
        Map<String, Object> body = new HashMap<>();
        body.put(MESSAGE, "Validation failed");
        body.put("errors", errors);
        return Mono.just(ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body));
    }
}
