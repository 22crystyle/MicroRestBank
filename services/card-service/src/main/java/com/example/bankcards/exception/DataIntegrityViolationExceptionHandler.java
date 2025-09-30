package com.example.bankcards.exception;

import com.example.shared.dto.response.RestErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A global exception handler for data integrity violation exceptions.
 *
 * <p>This class uses {@link RestControllerAdvice} to capture {@link DataIntegrityViolationException}
 * instances, which typically occur when a database constraint is violated (e.g., unique key
 * constraint). It provides a structured error response with an HTTP 409 (Conflict) status.</p>
 */
@RestControllerAdvice
public class DataIntegrityViolationExceptionHandler {

    private final String applicationName;

    /**
     * Constructs a new {@code DataIntegrityViolationExceptionHandler} with the application name.
     *
     * @param applicationName The name of the application, injected from the Spring environment.
     */
    public DataIntegrityViolationExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Handles {@link DataIntegrityViolationException}.
     *
     * <p>This method is triggered when a data integrity constraint is violated in the database.
     * It attempts to extract the constraint name from the underlying Hibernate exception to provide
     * a more specific error message. It returns an HTTP 409 (Conflict) status.</p>
     *
     * @param ex The caught {@link DataIntegrityViolationException}.
     * @return A {@link ResponseEntity} containing a {@link RestErrorResponse} and an HTTP 409 status.
     */
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
