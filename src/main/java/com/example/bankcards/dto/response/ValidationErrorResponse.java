package com.example.bankcards.dto.response;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationErrorResponse extends ErrorResponse {
    private final Map<String, String> errors;

    public ValidationErrorResponse(int status, String message, Map<String, String> errors) {
        super(status, message);
        this.errors = errors;
    }

}
