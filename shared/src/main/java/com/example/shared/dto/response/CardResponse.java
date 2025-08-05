package com.example.shared.dto.response;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String pan,
        UserResponse owner,
        CardStatusResponse status,
        BigDecimal balance
) {
}
