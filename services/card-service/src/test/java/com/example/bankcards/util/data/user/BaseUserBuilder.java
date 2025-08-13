package com.example.bankcards.util.data.user;

import java.util.UUID;

public abstract class BaseUserBuilder<T extends BaseUserBuilder<T>> {
    protected UUID id;
    protected String username = "user";
    protected String password = "pass";
    protected String firstName = "First";
    protected String lastName = "Last";

    public T withId(UUID id) {
        this.id = id;
        return self();
    }

    public T withUsername(String username) {
        this.username = username;
        return self();
    }

    public T withPassword(String password) {
        this.password = password;
        return self();
    }

    public T withFirstName(String firstName) {
        this.firstName = firstName;
        return self();
    }

    public T withLastName(String lastName) {
        this.lastName = lastName;
        return self();
    }

    protected abstract T self();
}
