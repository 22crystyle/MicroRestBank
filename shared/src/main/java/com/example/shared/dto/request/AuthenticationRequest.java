package com.example.shared.dto.request;

public record AuthenticationRequest(
        String username,
        String password
) {
}
