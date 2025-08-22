package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(UUID userId) {
        super("Cards for user with id=" + userId + " not found");
    }
}
