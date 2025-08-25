package com.example.auth.config;


import com.example.auth.util.JwtMonoConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/docs", "/v3/**", "/swagger-ui/**", "/api/v1/auth/**", "/healthcheck", "/actuator/health").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((exchange1, denied) ->
                                Mono.fromRunnable(() -> exchange1.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                        )
                        .authenticationEntryPoint((exchange1, denied) ->
                                Mono.fromRunnable(() -> exchange1.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        )
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return new JwtMonoConverter();
    }
}