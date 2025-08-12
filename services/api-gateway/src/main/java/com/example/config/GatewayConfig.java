package com.example.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.rewritePath("/auth", "/api/v1/auth"))
                        .uri("lb://auth-service"))

                .route("customer-service", r -> r.path("/customers/**")
                        .filters(f -> f.tokenRelay()
                                .addRequestHeader("X-Service-Call", "gateway")
                                .rewritePath("/customers", "/api/v1/customers"))
                        .uri("lb://customer-service"))

                .route("card-service", r -> r.path("/cards/**")
                        .filters(f -> f
                                .tokenRelay()
                                .rewritePath("/cards", "/api/v1/cards")
                                .circuitBreaker(config -> config
                                        .setName("cardCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                ))
                        .uri("lb://card-service"))

                .route("transaction-service", r -> r.path("/transactions/**")
                        .uri("lb://transaction-service"))

                .build();
    }
}
