package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

public class CardNotFoundException extends EntityNotFoundException {
    public CardNotFoundException(Long cardId) {
        super("Card with id=" + cardId + " not found");
    }

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException() {
        super("Card not found");
    }
}
