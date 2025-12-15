package org.restbank.libs.api.exception;

public abstract class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) { //TODO: SonarQube
        super(message);
    }
}
