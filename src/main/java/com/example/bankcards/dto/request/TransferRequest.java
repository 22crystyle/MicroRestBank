package com.example.bankcards.dto.request;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
