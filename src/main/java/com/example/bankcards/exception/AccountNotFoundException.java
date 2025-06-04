package com.example.bankcards.exception;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException(Long accountId) {
        super("Account with id=" + accountId + " not found");
    }
}
