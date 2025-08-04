package com.example.bankcards.exception;

public class CardStatusNotFoundException extends EntityNotFoundException {
    public CardStatusNotFoundException(int cardStatusId) {
        super("Unknown status with id=" + cardStatusId);
    }
}
