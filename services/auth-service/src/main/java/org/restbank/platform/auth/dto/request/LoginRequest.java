package org.restbank.platform.auth.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
