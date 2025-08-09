package com.example.customer.dto.request;

import lombok.ToString;

import java.util.UUID;

public record CustomerRequest(
    UUID id,
    String username,
    String firstName,
    String lastName,
    String email
) {
}
