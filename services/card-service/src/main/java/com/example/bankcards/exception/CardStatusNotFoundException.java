package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

public class CardStatusNotFoundException extends EntityNotFoundException {
    public CardStatusNotFoundException(int cardStatusId) {
        super("Unknown status with id=" + cardStatusId);
    }
}
