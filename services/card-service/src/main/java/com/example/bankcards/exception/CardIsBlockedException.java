package com.example.bankcards.exception;

/**
 * An exception thrown when an operation is attempted on a card that is blocked.
 */
public class CardIsBlockedException extends RuntimeException {

    /**
     * Constructs a new {@code CardIsBlockedException} with a detail message indicating
     * which card is blocked.
     *
     * @param id The ID of the blocked card.
     */
    public CardIsBlockedException(Long id) {
        super("Card with id=" + id + " is blocked.");
    }
}
