package com.example.bankcards.exception;

import com.example.shared.dto.response.RestErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DataIntegrityViolationExceptionHandler {

    private final String applicationName;

    public DataIntegrityViolationExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        String message = "Data integrity violation";
        if (cause instanceof org.hibernate.exception.ConstraintViolationException hce) {
            String constraint = hce.getConstraintName();
            message = "Нарушено ограничение: " + constraint;
        }
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.CONFLICT.toString(), message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
