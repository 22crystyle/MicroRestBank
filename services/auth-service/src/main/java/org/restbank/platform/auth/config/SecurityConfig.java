package org.restbank.platform.auth.config;


import lombok.RequiredArgsConstructor;
import org.restbank.libs.api.security.JwtConverter;
import org.restbank.platform.auth.config.beans.CustomAccessDeniedHandler;
import org.restbank.platform.auth.config.beans.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Configures the security settings for the Authentication Service.
 *
 * <p>This class defines the security filter chain, whitelisted paths, and JWT
 * converter for handling authentication and authorization.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * An array of paths that are permitted to be accessed without authentication.
     * This includes health checks, API documentation, and authentication endpoints.
     */
    private static final String[] AUTH_WHITELIST = {
            "/actuator/health",
            "/v3/api-docs",
            "/api/v1/auth/**"
    };

    /**
     * Creates and configures the main security filter chain for the service.
     *
     * <p>This filter chain disables CSRF, defines authorization rules, and sets up
     * custom exception handling for authentication and authorization failures.
     *
     * @param http The {@link ServerHttpSecurity} to be configured.
     * @return The configured {@link SecurityWebFilterChain}.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }

    /**
     * Provides a converter to extract authorities from a JWT.
     *
     * @return A {@link Converter} that transforms a {@link Jwt} into an
     * {@link AbstractAuthenticationToken}.
     */
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return new ReactiveJwtAuthenticationConverterAdapter(new JwtConverter());
    }
}