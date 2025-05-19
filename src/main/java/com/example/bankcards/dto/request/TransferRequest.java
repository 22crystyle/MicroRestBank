package com.example.bankcards.dto.request;

public record TransferRequest(
        String fromCard,
        String toCard
) {
}
