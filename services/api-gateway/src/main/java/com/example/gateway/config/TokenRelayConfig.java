package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configures the WebClient to relay OAuth2 tokens.
 *
 * <p>This class sets up a {@link WebClient} bean that is capable of
 * automatically adding the OAuth2 access token to outgoing requests.
 */
@Configuration
public class TokenRelayConfig {

    /**
     * Creates a {@link WebClient} bean configured with an OAuth2 filter.
     *
     * <p>This WebClient will automatically handle the OAuth2 token relay,
     * making it easy to communicate with other resource servers.
     *
     * @param clientRegistration The repository of client registrations.
     * @param authorizedClients The service for authorized clients.
     * @return A configured {@link WebClient} instance.
     */
    @Bean
    public WebClient webClient(ReactiveClientRegistrationRepository clientRegistration,
                               ReactiveOAuth2AuthorizedClientService authorizedClients) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistration,
                        new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(authorizedClients)
                );

        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter)
                .build();
    }
}
