package com.example.auth.exception;

import jakarta.persistence.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(int id) {
        super("Role with id " + id + " not found");
    }
}
