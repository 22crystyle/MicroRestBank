package com.example.bankcards.exception;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(int id) {
        super("Role with id " + id + " not found");
    }
}
