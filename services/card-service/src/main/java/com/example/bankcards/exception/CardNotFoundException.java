package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

/**
 * An exception thrown when a requested card cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a {@link com.example.bankcards.entity.Card} entity yielded no results.</p>
 */
public class CardNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new {@code CardNotFoundException} with a detail message indicating
     * which card was not found.
     *
     * @param cardId The ID of the card that could not be found.
     */
    public CardNotFoundException(Long cardId) {
        super("Card with id=" + cardId + " not found");
    }

    /**
     * Constructs a new {@code CardNotFoundException} with a custom message.
     *
     * @param message The detail message.
     */
    public CardNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CardNotFoundException} with a default message.
     */
    public CardNotFoundException() {
        super("Card not found");
    }
}
