package org.restbank.service.card.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;
import org.restbank.service.card.entity.User;

import java.util.UUID;

/**
 * An exception thrown when a requested user cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a {@link User} entity yielded no results.</p>
 */
public class UserNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new {@code UserNotFoundException} with a detail message indicating
     * which user was not found.
     *
     * @param userId The UUID of the user that could not be found.
     */
    public UserNotFoundException(UUID userId) {
        super("User with id=" + userId + " not found");
    }
}
