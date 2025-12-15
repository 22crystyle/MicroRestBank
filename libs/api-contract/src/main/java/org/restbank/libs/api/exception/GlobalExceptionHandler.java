package org.restbank.libs.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.restbank.libs.api.dto.response.RestErrorResponse;
import org.restbank.libs.api.dto.response.ValidationErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
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
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final String applicationName;

    public GlobalExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    // 404 Entity from database not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFoundException(EntityNotFoundException ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 403 Forbidden: User don't have enough privileges
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.FORBIDDEN.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 400 Bad Request for validation errors
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid( //TODO: SonarQube
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ValidationErrorResponse response = new ValidationErrorResponse(applicationName, status.toString(), "Validation Error", errors);
        return new ResponseEntity<>(response, status);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal( //TODO: SonarQube
            @NonNull Exception ex,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request
    ) {
        RestErrorResponse errorResponse;

        if (body instanceof RestErrorResponse) { //TODO: SonarQube
            errorResponse = (RestErrorResponse) body;
        } else if (body instanceof ProblemDetail) {
            String fullMsg = ex.getMessage();
            String shortMsg = (fullMsg != null && fullMsg.contains(":"))
                    ? fullMsg.substring(0, fullMsg.indexOf(":"))
                    : fullMsg;
            errorResponse = RestErrorResponse.builder()
                    .source(applicationName)
                    .code(statusCode.toString())
                    .message(shortMsg)
                    .build();
        } else {
            String message = body != null
                    ? body.toString()
                    : Optional.ofNullable(ex.getMessage()).orElse(statusCode.toString());

            errorResponse = RestErrorResponse.builder()
                    .source(applicationName)
                    .code(statusCode.toString())
                    .message(message)
                    .build();
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }


    // 500 Internal Server Error for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleApiError(Exception ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        log.error(Arrays.toString(ex.getStackTrace()));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}