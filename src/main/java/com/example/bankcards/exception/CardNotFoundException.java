package com.example.bankcards.exception;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(Long cardId) {
        super("Card with id="+cardId+" not found");
    }
}
