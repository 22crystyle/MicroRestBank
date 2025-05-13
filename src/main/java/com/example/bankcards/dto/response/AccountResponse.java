package com.example.bankcards.dto.response;

public record AccountResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}
