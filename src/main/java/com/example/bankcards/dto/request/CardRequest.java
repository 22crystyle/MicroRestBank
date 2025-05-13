package com.example.bankcards.dto.request;

import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.CardStatus;

import java.time.YearMonth;

public record CardRequest(
        Integer id,
        Long cardNumber,
        AccountResponse owner,
        YearMonth expiryDate,
        CardStatus status
) {
}
