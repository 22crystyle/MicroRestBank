package org.restbank.libs.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts a {@link Jwt} into an {@link AbstractAuthenticationToken} by extracting
 * authorities (roles) from the JWT claims.
 * This converter is used to integrate Keycloak JWTs with Spring Security.
 */
@RequiredArgsConstructor
@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Converts the provided JWT into an authentication token with extracted authorities.
     *
     * @param jwt The JWT to convert.
     * @return An {@link AbstractAuthenticationToken} containing the JWT and extracted authorities.
     */
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    /**
     * Extracts granted authorities (roles) from the JWT's 'realm_access' claim.
     *
     * @param jwt The JWT from which to extract authorities.
     * @return A collection of {@link GrantedAuthority} representing the user's roles.
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return List.of();
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
