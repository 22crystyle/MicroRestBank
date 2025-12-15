package org.restbank.service.card.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restbank.libs.api.security.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

/**
 * Configuration class for security settings within the card service.
 *
 * <p>This class enables web security and method-level security, providing a configuration
 * for the security filter chain, password encoding, and the authentication manager. It defines
 * publicly accessible endpoints (whitelisting) and configures the resource server to use JWT-based
 * authentication.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * An array of URL patterns that are publicly accessible and do not require authentication.
     * This typically includes health check endpoints and API documentation.
     */
    private static final String[] AUTH_WHITELIST = {
            "/actuator/health",
            "/v3/api-docs/**"
    };

    /**
     * A converter to extract authorities from a JWT and configure the authentication token.
     */
    private final JwtAuthConverter jwtAuthConverter;

    /**
     * Defines the security filter chain for the application.
     *
     * <p>This bean configures the following security aspects:
     * <ul>
     *     <li>Disables CSRF (Cross-Site Request Forgery) protection, as the service is stateless.</li>
     *     <li>Configures session management to be stateless.</li>
     *     <li>Defines authorization rules, permitting access to whitelisted paths and requiring
     *         authentication for all other requests.</li>
     *     <li>Sets up exception handling for access denied scenarios.</li>
     *     <li>Configures the OAuth2 resource server to validate JWTs using the provided
     *         {@link JwtAuthConverter}.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @return A {@link SecurityFilterChain} that defines the security rules for the application.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(new AccessDeniedHandlerImpl()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                );
        return http.build();
    }

    /**
     * Provides a password encoder bean for hashing passwords.
     *
     * <p>This implementation uses the {@link BCryptPasswordEncoder}, which is a strong,
     * widely-used hashing algorithm for storing passwords securely.</p>
     *
     * @return A {@link PasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} as a bean.
     *
     * <p>This bean is retrieved from the {@link AuthenticationConfiguration} and is used
     * to process authentication requests within the Spring Security framework.</p>
     *
     * @param authConfig The authentication configuration provided by Spring Boot.
     * @return The configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
