package com.example.shared.dto.response;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationErrorResponse extends RestErrorResponse {
    private final Map<String, String> errors;

    public ValidationErrorResponse(String source, String code, String message, Map<String, String> errors) {
        super(source, code, message);
        this.errors = errors;
    }
}
