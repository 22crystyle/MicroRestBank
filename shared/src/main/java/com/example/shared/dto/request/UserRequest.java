package com.example.shared.dto.request;

import com.example.shared.annotations.FieldNotEmpty;

public record UserRequest(
        @FieldNotEmpty(field = "{field.username}")
        String username,
        @FieldNotEmpty(field = "{field.password}")
        String password,
        @FieldNotEmpty(field = "{field.firstName}")
        String firstName,
        String lastName,
        String email,
        String phone,
        Integer roleId
) {
}