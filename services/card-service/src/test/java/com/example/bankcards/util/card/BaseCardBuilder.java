package com.example.bankcards.util.card;

import java.math.BigDecimal;

public abstract sealed class BaseCardBuilder<T extends BaseCardBuilder<T>> permits CardData.CardBuilder, CardData.CardResponseBuilder {
    protected Long id;
    protected String pan = "1234 1234 1234 1234";
    protected BigDecimal balance = new BigDecimal(200);

    public T withId(Long id) {
        this.id = id;
        return self();
    }

    public T withPan(String pan) {
        this.pan = pan;
        return self();
    }

    public T withBalance(BigDecimal balance) {
        this.balance = balance;
        return self();
    }

    protected abstract T self();
}
