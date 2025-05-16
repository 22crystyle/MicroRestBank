package com.example.bankcards.dto.response;

public record CardStatusResponse(
        int id,
        String description,
        String status
) {
}
