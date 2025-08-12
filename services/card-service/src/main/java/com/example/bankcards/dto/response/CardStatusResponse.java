package com.example.bankcards.dto.response;

public record CardStatusResponse(
        Integer id,
        String description,
        String name
) {
}