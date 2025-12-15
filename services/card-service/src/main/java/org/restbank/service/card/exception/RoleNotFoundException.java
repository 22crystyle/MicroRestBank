package org.restbank.service.card.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;

/**
 * An exception thrown when a requested role cannot be found.
 *
 * <p>This exception extends {@link EntityNotFoundException} and is used to indicate that
 * a search for a role entity yielded no results.</p>
 */
public class RoleNotFoundException extends EntityNotFoundException {

    /**
     * Constructs a new {@code RoleNotFoundException} with a custom message.
     *
     * @param message The detail message.
     */
    public RoleNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code RoleNotFoundException} with a detail message indicating
     * which role was not found.
     *
     * @param id The ID of the role that could not be found.
     */
    public RoleNotFoundException(int id) {
        super("Role with id " + id + " not found");
    }
}
