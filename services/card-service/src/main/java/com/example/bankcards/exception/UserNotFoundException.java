package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(UUID userId) {
        super("User with id=" + userId + " not found");
    }
}
