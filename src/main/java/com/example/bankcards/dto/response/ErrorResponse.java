package com.example.bankcards.dto.response;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(
            String code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ErrorResponse that)) return false;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "ErrorResponse[" +
                "status=" + code + ", " +
                "message=" + message + ']';
    }

}
