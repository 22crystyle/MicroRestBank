package com.example.bankcards.dto.response;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(
            int status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public int status() {
        return status;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ErrorResponse) obj;
        return this.status == that.status &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }

    @Override
    public String toString() {
        return "ErrorResponse[" +
                "status=" + status + ", " +
                "message=" + message + ']';
    }

}
