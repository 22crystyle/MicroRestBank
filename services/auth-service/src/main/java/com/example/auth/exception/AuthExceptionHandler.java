package com.example.auth.exception;

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

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(KeycloakTokenException.class)
    public Mono<ResponseEntity<Object>> handleKeycloakTokenException(KeycloakTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("source", "keycloak");
        body.put("status", ex.getStatus().value());
        body.putAll(ex.getDetails());
        return Mono.just(ResponseEntity
                .status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    @ExceptionHandler(UserCreationException.class)
    public Mono<ResponseEntity<Object>> handleUserCreationException(UserCreationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @ExceptionHandler(AdminTokenException.class)
    public Mono<ResponseEntity<Object>> handleAdminTokenException(AdminTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Object>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("errors", errors);
        return Mono.just(ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body));
    }
}
