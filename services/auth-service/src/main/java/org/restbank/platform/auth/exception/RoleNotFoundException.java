package org.restbank.platform.auth.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(int id) {
        super("Role with id " + id + " not found");
    }
}
