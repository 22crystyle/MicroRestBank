package com.example.customer.exception;

import com.example.shared.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Exception thrown when a customer is not found.
 */
public class CustomerNotFound extends EntityNotFoundException {
    /**
     * Constructs a new CustomerNotFound exception with the specified UUID.
     * @param uuid The UUID of the customer that was not found.
     */
    public CustomerNotFound(UUID uuid) {
        super("Customer with uuid: " + uuid + " not found.");
    }
}
