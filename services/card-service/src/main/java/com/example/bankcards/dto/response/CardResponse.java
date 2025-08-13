package com.example.bankcards.dto.response;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String pan,
        UserResponse user,
        CardStatusResponse status,
        BigDecimal balance
) {
}
