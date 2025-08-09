package com.example.customer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        Instant created_at
) {
}
