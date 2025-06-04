package com.example.bankcards.exception;

public class CardStatusNotFoundException extends NotFoundException {
    public CardStatusNotFoundException(int cardStatusId) {
        super("Unknown status with id=" + cardStatusId);
    }
}
