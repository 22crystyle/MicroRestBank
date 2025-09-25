package com.example.shared.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestErrorResponse {
    private final String source;
    private final String code;
    private final String message;

    public RestErrorResponse(
            String source,
            String code,
            String message
    ) {
        this.source = source;
        this.code = code;
        this.message = message;
    }
}
