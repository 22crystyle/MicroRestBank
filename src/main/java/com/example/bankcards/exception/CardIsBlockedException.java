package com.example.bankcards.exception;

public class CardIsBlockedException extends RuntimeException {
    public CardIsBlockedException() {
        super("Card is blocked.");
    }
}
