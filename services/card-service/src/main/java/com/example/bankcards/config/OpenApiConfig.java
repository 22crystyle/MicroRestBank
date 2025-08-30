package com.example.bankcards.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer cardOpenApiCustomizer() {
        return openApi -> openApi.addServersItem(
                new Server()
                        .url("http://localhost:1024/cards")
                        .description("Card Service ENV")
        );
    }
}