package com.example.customer.exception;

import com.example.shared.exception.EntityNotFoundException;

import java.util.UUID;

public class CustomerNotFound extends EntityNotFoundException {
    public CustomerNotFound(UUID uuid) {
        super("Customer with uuid: " + uuid + " not found.");
    }
}
