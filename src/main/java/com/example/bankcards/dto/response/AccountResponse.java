package com.example.bankcards.dto.response;

import com.example.bankcards.entity.Role;

public record AccountResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role
) {
}