package com.example.bankcards.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestErrorResponse {
    private final String code;
    private final String message;

    public RestErrorResponse(
            String code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }
}
