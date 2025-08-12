package com.example.bankcards.exception;

import com.example.bankcards.dto.response.RestErrorResponse;
import com.example.bankcards.dto.response.ValidationErrorResponse;
import com.example.shared.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // 404 Entity from database not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFoundException(EntityNotFoundException ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 403 Forbidden: User don't have enough privileges
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.FORBIDDEN.toString(), ex.getMessage());
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
    public ResponseEntity<RestErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        String message = "Data integrity violation";
        if (cause instanceof org.hibernate.exception.ConstraintViolationException hce) {
            String constraint = hce.getConstraintName();
            message = "Нарушено ограничение: " + constraint;
        }
        RestErrorResponse error = new RestErrorResponse(HttpStatus.CONFLICT.toString(), message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 423 Locked: Card is blocked and unavailable for changes
    @ExceptionHandler(CardIsBlockedException.class)
    public ResponseEntity<RestErrorResponse> handleCardIsBlockedException(CardIsBlockedException ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.LOCKED.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.LOCKED);
    }

    // 403 Forbidden: User tries to use someone else's card
    @ExceptionHandler(IsNotOwnerException.class)
    public ResponseEntity<RestErrorResponse> handleIsNotOwnerException(IsNotOwnerException ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.FORBIDDEN.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 422 Unprocessable Entity: User send correct request, but the business rules do not allow the operation to be performed
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<RestErrorResponse> handleUnsupportedEntity(InvalidAmountException ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        RestErrorResponse errorResponse;

        if (body instanceof RestErrorResponse) {
            errorResponse = (RestErrorResponse) body;
        } else if (body instanceof ProblemDetail pd) {
            String fullMsg = ex.getMessage();
            String shortMsg = (fullMsg != null && fullMsg.contains(":"))
                    ? fullMsg.substring(0, fullMsg.indexOf(":"))
                    : fullMsg;
            errorResponse = RestErrorResponse.builder()
                    .code(statusCode.toString())
                    .message(shortMsg)
                    .build();
        } else {
            String message = body != null
                    ? body.toString()
                    : Optional.ofNullable(ex.getMessage()).orElse(statusCode.toString());

            errorResponse = RestErrorResponse.builder()
                    .code(statusCode.toString())
                    .message(message)
                    .build();
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }


    // 500 Internal Server Error for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleApiError(Exception ex) {
        RestErrorResponse error = new RestErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        log.error(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
