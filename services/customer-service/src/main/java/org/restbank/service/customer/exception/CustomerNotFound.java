package org.restbank.service.customer.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Exception thrown when a customer is not found.
 */
public class CustomerNotFound extends EntityNotFoundException {
    /**
     * Constructs a new CustomerNotFound exception with the specified UUID.
     *
     * @param uuid The UUID of the customer that was not found.
     */
    public CustomerNotFound(UUID uuid) {
        super("Customer with uuid: " + uuid + " not found.");
    }
}
