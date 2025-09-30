package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configures the security settings for the API Gateway.
 *
 * <p>This class defines the security filter chain, CORS configuration, and whitelisted
 * paths that do not require authentication.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * An array of paths that are permitted to be accessed without authentication.
     * This includes health checks, authentication endpoints, and API documentation.
     */
    private static final String[] AUTH_WHITELIST = {
            "/actuator/health",
            "/api/v1/auth/**",
            "/docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/customers/v3/api-docs",
            "/api/v1/cards/v3/api-docs",
            "/api/v1/auth/v3/api-docs"
    };

    /**
     * Creates and configures the main security filter chain for the gateway.
     *
     * <p>This filter chain enables CORS, defines authorization rules for all exchanges,
     * configures JWT-based resource server support, and disables CSRF protection.
     *
     * @param http The {@link ServerHttpSecurity} to be configured.
     * @return The configured {@link SecurityWebFilterChain}.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(Customizer.withDefaults()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    /**
     * Configures the Cross-Origin Resource Sharing (CORS) settings for the application.
     *
     * <p>This configuration allows requests from {@code http://localhost:1024} and
     * specifies the permitted HTTP methods and headers.
     *
     * @return A {@link CorsConfigurationSource} with the defined CORS rules.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:1024"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
