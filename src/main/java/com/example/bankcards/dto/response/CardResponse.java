package com.example.bankcards.dto.response;

import java.math.BigDecimal;

public record CardResponse(
        int id,
        String cardNumber,
        AccountResponse owner,
        CardStatusResponse status,
        BigDecimal balance
) {
}
