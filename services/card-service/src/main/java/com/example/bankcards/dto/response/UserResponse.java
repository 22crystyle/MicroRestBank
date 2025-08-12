package com.example.bankcards.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String firstName,
        String lastName
) {
}