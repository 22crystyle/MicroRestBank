package com.example.bankcards.exception;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long accountId) {
        super("User with id=" + accountId + " not found");
    }

    public UserNotFoundException(String username) {
        super("User with username=" + username + " not found");
    }
}
