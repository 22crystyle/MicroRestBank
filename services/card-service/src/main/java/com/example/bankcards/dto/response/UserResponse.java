package com.example.bankcards.dto.response;

import com.example.shared.dto.event.CustomerStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        CustomerStatus status
) {
}