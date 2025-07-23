package com.example.bankcards.exception;

public class CardNotFoundException extends EntityNotFoundException {
    public CardNotFoundException(Long cardId) {
        super("Card with id=" + cardId + " not found");
    }

    public CardNotFoundException() {
        super("Card not found");
    }
}
