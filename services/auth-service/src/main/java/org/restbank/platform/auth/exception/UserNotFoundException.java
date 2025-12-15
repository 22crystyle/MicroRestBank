package org.restbank.platform.auth.exception;

import org.restbank.libs.api.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long accountId) {
        super("User with id=" + accountId + " not found");
    }

    public UserNotFoundException(String username) {
        super("User with username=" + username + " not found");
    }
}
