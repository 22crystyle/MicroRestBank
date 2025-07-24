package com.example.bankcards.util.data.account.role;

public abstract class BaseRoleBuilder<T extends BaseRoleBuilder<T>> {
    protected Integer id;
    protected String name = "USER";

    public T withId(Integer id) {
        this.id = id;
        return self();
    }

    public T withName(String name) {
        this.name = name;
        return self();
    }

    protected abstract T self();
}
