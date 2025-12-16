package org.restbank.libs.api.dto.event;

import java.util.Optional;

public enum EventType {
    CUSTOMER_CREATED,
    CUSTOMER_UPDATED,
    CUSTOMER_DELETED;

    public static Optional<EventType> from(String value) {
        try {
            return Optional.of(EventType.valueOf(value));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
