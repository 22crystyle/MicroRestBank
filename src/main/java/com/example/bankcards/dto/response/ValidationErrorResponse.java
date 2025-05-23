package com.example.bankcards.dto.response;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(int status, String message, Map<String, String> errors) {
        super(status, message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
