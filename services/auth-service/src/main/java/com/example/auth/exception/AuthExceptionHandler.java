package com.example.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

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
}
