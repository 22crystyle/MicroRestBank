package com.example.bankcards.exception;

import com.example.shared.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * An exception thrown when a requested user cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a {@link com.example.bankcards.entity.User} entity yielded no results.</p>
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
