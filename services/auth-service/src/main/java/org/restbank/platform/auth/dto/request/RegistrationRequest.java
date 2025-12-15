package org.restbank.platform.auth.dto.request;

import org.restbank.libs.api.annotations.FieldNotEmpty;

public record RegistrationRequest(
        @FieldNotEmpty(field = "{field.username}")
        String username,
        @FieldNotEmpty(field = "{field.password}")
        String password,
        @FieldNotEmpty(field = "{field.firstName}")
        String firstName,
        String lastName,
        String email
) {
}
