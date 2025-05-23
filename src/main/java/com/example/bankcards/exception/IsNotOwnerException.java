package com.example.bankcards.exception;

public class IsNotOwnerException extends RuntimeException {
    public IsNotOwnerException() {
        super();
    }

    public IsNotOwnerException(String message) {
        super(message);
    }

    public IsNotOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
