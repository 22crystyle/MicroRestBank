package com.example.bankcards.util.data.card;

public abstract class BaseCardBuilder<T extends BaseCardBuilder<T>> {
    protected Long id;
    protected String cardNumber = "1234 1234 1234 1234";

    public T withId(Long id) {
        this.id = id;
        return self();
    }

    public T withPan(String cardNumber) {
        this.cardNumber = cardNumber;
        return self();
    }

    protected abstract T self();
}
