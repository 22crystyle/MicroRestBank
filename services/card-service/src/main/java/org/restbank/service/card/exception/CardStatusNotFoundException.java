package org.restbank.service.card.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;
import org.restbank.service.card.entity.CardStatus;

/**
 * An exception thrown when a requested card status cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a {@link CardStatus} entity yielded no results.</p>
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
