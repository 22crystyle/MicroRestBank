package com.example.bankcards.exception;

import com.example.shared.dto.response.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CardExceptionHandler {
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
}
