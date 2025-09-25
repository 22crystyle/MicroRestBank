package com.example.customer.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customerOpenApiCustomizer() {
        return openApi -> {
            openApi.setServers(List.of(
                    new Server()
                            .url("http://localhost:1024/customers")
                            .description("Customer Service ENV"))
            );

            Map<String, io.swagger.v3.oas.models.PathItem> paths = openApi.getPaths();
            if (paths != null) {
                Map<String, io.swagger.v3.oas.models.PathItem> newPathsMap = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, io.swagger.v3.oas.models.PathItem> entry : paths.entrySet()) {
                    String oldPath = entry.getKey();
                    if (oldPath.startsWith("/api/v1/customers")) {
                        String newPath = oldPath.substring("/api/v1/customers".length());
                        if (newPath.isEmpty()) {
                            newPath = "";
                        }
                        newPathsMap.put(newPath, entry.getValue());
                    } else {
                        newPathsMap.put(oldPath, entry.getValue());
                    }
                }
                io.swagger.v3.oas.models.Paths pathsObject = new io.swagger.v3.oas.models.Paths();
                pathsObject.putAll(newPathsMap);
                openApi.setPaths(pathsObject);
            }
        };
    }
}
