package com.example.gateway.config;

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
                                .rewritePath("/customers/(?<path>.*)", "/api/v1/customers/${path}"))
                        .uri("lb://customer-service"))

                .route("card-service-api-docs", r -> r.path("/cards/v3/api-docs")
                        .filters(f -> f.rewritePath("/cards/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://card-service"))

                .route("card-service", r -> r.path("/cards/**")
                        .filters(f -> f
                                .tokenRelay()
                                .rewritePath("/cards/(?<path>.*)", "/api/v1/cards/${path}")
                                .circuitBreaker(config -> config
                                        .setName("cardCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                ))
                        .uri("lb://card-service"))

                .build();
    }
}
