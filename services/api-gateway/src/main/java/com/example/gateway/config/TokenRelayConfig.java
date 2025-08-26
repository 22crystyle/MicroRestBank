package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TokenRelayConfig {

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
