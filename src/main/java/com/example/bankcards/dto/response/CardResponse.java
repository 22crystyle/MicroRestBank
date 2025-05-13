package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;

public record CardResponse(
        int id,
        String cardNumber,
        AccountResponse owner,
        CardStatus status,
        BigDecimal balance
) {
}
