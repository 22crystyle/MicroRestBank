package org.restbank.service.card.util.user;

import org.restbank.libs.api.dto.event.CustomerStatus;

import java.util.UUID;

public abstract class BaseUserBuilder<T extends BaseUserBuilder<T>> {
    protected UUID id;
    protected CustomerStatus status = CustomerStatus.ACTIVE;

    public T withId(UUID id) {
        this.id = id;
        return self();
    }

    public T withStatus(CustomerStatus status) {
        this.status = status;
        return self();
    }

    protected abstract T self();
}
