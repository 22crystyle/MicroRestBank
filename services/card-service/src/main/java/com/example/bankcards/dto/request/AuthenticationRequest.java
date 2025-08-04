package com.example.bankcards.dto.request;

public record AuthenticationRequest(
        String username,
        String password
) {
}
