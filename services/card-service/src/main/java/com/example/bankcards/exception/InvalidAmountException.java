package com.example.bankcards.exception;

import java.math.BigDecimal;

/**
 * An exception thrown when an invalid amount is used in a financial transaction.
 *
 * <p>This exception is typically thrown when a transaction amount is not positive or when
 * there are insufficient funds for a withdrawal.</p>
 */
public class InvalidAmountException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidAmountException} with a detail message indicating
     * the invalid amount.
     *
     * @param amount The invalid amount that caused the exception.
     */
    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount);
    }

    /**
     * Constructs a new {@code InvalidAmountException} with a custom message.
     *
     * @param message The detail message.
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}
