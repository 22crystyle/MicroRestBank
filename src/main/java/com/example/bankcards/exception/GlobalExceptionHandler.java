package com.example.bankcards.exception;

import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.dto.response.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // 404 Entity from database not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 403 Forbidden: User don't have enough privileges
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 400 Bad Request for validation errors
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ValidationErrorResponse response = new ValidationErrorResponse(status.toString(), "Validation Error", errors);
        return new ResponseEntity<>(response, status);
    }

    // 409 Conflict for data integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 423 Locked: Card is blocked and unavailable for changes
    @ExceptionHandler(CardIsBlockedException.class)
    public ResponseEntity<ErrorResponse> handleCardIsBlockedException(CardIsBlockedException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.LOCKED.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.LOCKED);
    }

    // 403 Forbidden: User tries to use someone else's card
    @ExceptionHandler(IsNotOwnerException.class)
    public ResponseEntity<ErrorResponse> handleIsNotOwnerException(IsNotOwnerException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 

    // 500 Internal Server Error for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleApiError(Exception ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
