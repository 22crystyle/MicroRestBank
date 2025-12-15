package org.restbank.service.card.exception;

import org.restbank.libs.api.dto.response.RestErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A global exception handler for the card service.
 *
 * <p>This class uses {@link RestControllerAdvice} to capture and handle specific exceptions
 * thrown by the controllers. It provides a consistent, structured error response format
 * for various business-related exceptions, such as when a card is blocked or a user attempts
 * an unauthorized action.</p>
 */
@RestControllerAdvice
public class CardExceptionHandler {

    private final String applicationName;

    /**
     * Constructs a new {@code CardExceptionHandler} with the application name.
     *
     * @param applicationName The name of the application, injected from the Spring environment.
     */
    public CardExceptionHandler(@Value("${spring.application.name:unknown}") String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Handles the {@link CardIsBlockedException}.
     *
     * <p>This method is triggered when an operation is attempted on a blocked card. It returns
     * an HTTP 423 (Locked) status with a standardized error response.</p>
     *
     * @param ex The caught {@link CardIsBlockedException}.
     * @return A {@link ResponseEntity} containing a {@link RestErrorResponse} and an HTTP 423 status.
     */
    @ExceptionHandler(CardIsBlockedException.class)
    public ResponseEntity<RestErrorResponse> handleCardIsBlockedException(CardIsBlockedException ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.LOCKED.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.LOCKED);
    }

    /**
     * Handles the {@link IsNotOwnerException}.
     *
     * <p>This method is triggered when a user attempts to perform an action on a card they do not own.
     * It returns an HTTP 403 (Forbidden) status with a standardized error response.</p>
     *
     * @param ex The caught {@link IsNotOwnerException}.
     * @return A {@link ResponseEntity} containing a {@link RestErrorResponse} and an HTTP 403 status.
     */
    @ExceptionHandler(IsNotOwnerException.class)
    public ResponseEntity<RestErrorResponse> handleIsNotOwnerException(IsNotOwnerException ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.FORBIDDEN.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles the {@link InvalidAmountException}.
     *
     * <p>This method is triggered when a business rule related to a transaction amount is violated
     * (e.g., insufficient funds, non-positive amount). It returns an HTTP 422 (Unprocessable Entity)
     * status with a standardized error response.</p>
     *
     * @param ex The caught {@link InvalidAmountException}.
     * @return A {@link ResponseEntity} containing a {@link RestErrorResponse} and an HTTP 422 status.
     */
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<RestErrorResponse> handleUnsupportedEntity(InvalidAmountException ex) {
        RestErrorResponse error = new RestErrorResponse(applicationName, HttpStatus.UNPROCESSABLE_ENTITY.toString(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
