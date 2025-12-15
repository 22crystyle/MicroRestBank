package org.restbank.platform.auth.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A converter that transforms a {@link Jwt} into a {@link Mono} containing a
 * {@link JwtAuthenticationToken}.
 *
 * <p>This converter extracts roles from the "realm_access" claim and combines them
 * with authorities from the "scope" claim to create a complete set of authorities
 * for the authenticated user.
 */
public class JwtMonoConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    /**
     * Converts a {@link Jwt} into a {@link Mono} of {@link AbstractAuthenticationToken}.
     *
     * @param jwt The source {@link Jwt} object.
     * @return A {@link Mono} containing a {@link JwtAuthenticationToken} with the
     * extracted authorities.
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) { //TODO: SonarQube
        Collection<GrantedAuthority> auths = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            for (String role : roles) {
                auths.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        auths.addAll(scopeConverter.convert(jwt));

        return Mono.just(new JwtAuthenticationToken(jwt, auths));
    }
}
