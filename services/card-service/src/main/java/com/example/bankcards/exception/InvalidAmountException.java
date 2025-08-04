package com.example.bankcards.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount);
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}
