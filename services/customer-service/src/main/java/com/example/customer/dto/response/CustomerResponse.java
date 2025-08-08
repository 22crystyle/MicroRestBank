package com.example.customer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String first_name,
        String last_name,
        Instant created_at
) {
}
