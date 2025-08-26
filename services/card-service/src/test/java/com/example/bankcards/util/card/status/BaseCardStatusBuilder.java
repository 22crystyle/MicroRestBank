package com.example.bankcards.util.card.status;

public abstract class BaseCardStatusBuilder<T extends BaseCardStatusBuilder<T>> {
    protected Integer id;
    protected String name = "ACTIVE";

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
