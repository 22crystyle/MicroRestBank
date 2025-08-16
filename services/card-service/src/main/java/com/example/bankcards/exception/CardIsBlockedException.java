package com.example.bankcards.exception;

public class CardIsBlockedException extends RuntimeException {
    public CardIsBlockedException(Long id) {
        super("Card with id=" + id + " is blocked.");
    }
}
