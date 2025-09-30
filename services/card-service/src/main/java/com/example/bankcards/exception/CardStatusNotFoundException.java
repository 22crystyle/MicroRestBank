package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

/**
 * An exception thrown when a requested card status cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a {@link com.example.bankcards.entity.CardStatus} entity yielded no results.</p>
 */
public class CardStatusNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new {@code CardStatusNotFoundException} with a detail message indicating
     * which card status was not found.
     *
     * @param cardStatusId The ID of the card status that could not be found.
     */
    public CardStatusNotFoundException(int cardStatusId) {
        super("Unknown status with id=" + cardStatusId);
    }
}
