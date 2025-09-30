package com.example.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Keycloak integration.
 *
 * @param serverUrl The URL of the Keycloak server.
 * @param realm The realm to be used for authentication.
 * @param clientId The client ID for this application.
 * @param clientSecret The client secret for this application.
 */
@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret
) {
}
