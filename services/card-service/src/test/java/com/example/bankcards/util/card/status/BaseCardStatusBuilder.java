package com.example.bankcards.util.card.status;

import com.example.bankcards.entity.CardStatusType;

public abstract class BaseCardStatusBuilder<T extends BaseCardStatusBuilder<T>> {
    protected Integer id;
    protected CardStatusType status = CardStatusType.ACTIVE;

    public T withId(Integer id) {
        this.id = id;
        return self();
    }

    public T withName(CardStatusType status) {
        this.status = status;
        return self();
    }

    protected abstract T self();
}
