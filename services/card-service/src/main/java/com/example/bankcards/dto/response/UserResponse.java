package com.example.bankcards.dto.response;

import com.example.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role
) {
}