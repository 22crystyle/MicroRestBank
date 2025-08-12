package com.example.bankcards.dto.request;

import com.example.shared.annotations.FieldNotEmpty;

public record UserRequest(
        @FieldNotEmpty(field = "{field.username}")
        String username,
        @FieldNotEmpty(field = "{field.firstName}")
        String firstName,
        String lastName
) {
}