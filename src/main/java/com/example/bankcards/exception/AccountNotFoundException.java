package com.example.bankcards.exception;

public class AccountNotFoundException extends EntityNotFoundException {
    public AccountNotFoundException(Long accountId) {
        super("Account with id=" + accountId + " not found");
    }

    public AccountNotFoundException(String username) {
        super("Account with username=" + username + " not found");
    }
}
