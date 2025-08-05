package com.example.shared.dto.response;

import com.example.shared.entity.Role;

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