package com.example.bankcards.dto.response;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String cardNumber,
        AccountResponse owner,
        CardStatusResponse status,
        BigDecimal balance
) {
}
