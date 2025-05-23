package com.example.bankcards.exception;

public class CardIsBlockedException extends RuntimeException {
    public CardIsBlockedException(String message) {
        super(message);
    }
}
