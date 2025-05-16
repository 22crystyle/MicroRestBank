package com.example.bankcards.dto.request;

public record AccountRequest(
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        String phone,
        Long role_id
) {
}
