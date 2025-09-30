package com.example.shared.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class SharedOpenApiConfiguration {

    @Value("${springdoc.api-path-segment}")
    private String apiPathSegment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenApiCustomizer sharedOpenApiCustomizer() {
        String serviceBaseUrl = "http://localhost:1024/api/v1/" + apiPathSegment;
        String pathPrefixToStrip = "/api/v1/" + apiPathSegment;

        return openApi -> {
            openApi.setServers(List.of(
                    new Server().url(serviceBaseUrl)
                            .description(applicationName + " ENV")
            ));

            Map<String, PathItem> paths = openApi.getPaths();
            if (paths != null) {
                Map<String, PathItem> newPathsMap = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
                    String oldPath = entry.getKey();
                    if (oldPath.startsWith(pathPrefixToStrip)) {
                        String newPath = oldPath.substring(pathPrefixToStrip.length());
                        if (!newPath.isEmpty() && !newPath.startsWith("/")) {
                            newPath = "/" + newPath;
                        }
                        newPathsMap.put(newPath, entry.getValue());
                    } else {
                        newPathsMap.put(oldPath, entry.getValue());
                    }
                }
                Paths pathsObject = new Paths();
                pathsObject.putAll(newPathsMap);
                openApi.setPaths(pathsObject);
            }
        };
    }
}
