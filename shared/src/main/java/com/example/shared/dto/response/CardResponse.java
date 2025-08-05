package com.example.shared.dto.response;

import com.example.shared.dto.response.CardStatusResponse;
import com.example.shared.dto.response.UserResponse;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String pan,
        UserResponse owner,
        CardStatusResponse status,
        BigDecimal balance
) {
}
