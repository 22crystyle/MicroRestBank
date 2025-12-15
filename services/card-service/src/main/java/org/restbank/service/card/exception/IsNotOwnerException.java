package org.restbank.service.card.exception;

/**
 * An exception thrown when a user attempts to access or modify a resource they do not own.
 *
 * <p>This exception is used to enforce ownership-based access control, for example, ensuring
 * that a user can only perform operations on their own bank cards.</p>
 */
public class IsNotOwnerException extends RuntimeException {

    /**
     * Constructs a new {@code IsNotOwnerException} with no detail message.
     */
    public IsNotOwnerException() {
        super();
    }

    /**
     * Constructs a new {@code IsNotOwnerException} with the specified detail message.
     *
     * @param message The detail message.
     */
    public IsNotOwnerException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code IsNotOwnerException} with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public IsNotOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
