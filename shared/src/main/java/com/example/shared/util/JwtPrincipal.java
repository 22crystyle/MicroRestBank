package com.example.shared.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtPrincipal {
    public static String getUsername(Authentication auth) {
        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("preferred_username");
        }

        return auth.getName();
    }

    public static String getId(Authentication auth) {
        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "";
    }
}
