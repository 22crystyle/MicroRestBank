package org.restbank.service.customer.exception;

import org.restbank.libs.api.dto.response.RestErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for DataIntegrityViolationException.
 * Converts database constraint violation exceptions into a user-friendly REST error response.
 */
@RestControllerAdvice
public class DataIntegrityViolationExceptionHandler {

    private final String applicationName;

    /**
     * Constructs a new DataIntegrityViolationExceptionHandler.
     *
     * @param applicationName The name of the application, injected from Spring properties.
     */
    public DataIntegrityViolationExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Handles DataIntegrityViolationException and returns a CONFLICT status with a custom error message.
     *
     * @param ex The DataIntegrityViolationException that was thrown.
     * @return A ResponseEntity containing a RestErrorResponse with conflict details.
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
