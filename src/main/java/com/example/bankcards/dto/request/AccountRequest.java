package com.example.bankcards.dto.request;

import com.example.bankcards.annotations.FieldNotEmpty;

public record AccountRequest(
        @FieldNotEmpty(message = "{validation.notEmpty}", field = "{field.username}")
        String username,
        @FieldNotEmpty(message = "{validation.notEmpty}", field = "{field.password}")
        String password,
        String firstName,
        String lastName,
        String email,
        String phone,
        Integer role_id
) {
}
