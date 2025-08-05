package com.example.auth.dto.request;

public record AuthenticationRequest(
        String username,
        String password
) {
}
