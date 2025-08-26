package com.example.auth.service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegistrationRequest;
import com.example.auth.dto.response.TokenResponse;
import com.example.auth.exception.KeycloakTokenException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private WebClient client() {
        return webClientBuilder.baseUrl(serverUrl).build();
    }

    public Mono<TokenResponse> login(LoginRequest loginRequest) {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", loginRequest.username())
                        .with("password", loginRequest.password()))
                .exchangeToMono(resp -> {
                    if (resp.statusCode().is2xxSuccessful()) {
                        return resp.bodyToMono(TokenResponse.class);
                    } else {
                        return resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        JsonNode node = MAPPER.readTree(body);
                                        String err = node.has("error") ? node.get("error").asText() : null;
                                        String desc = node.has("error_description") ? node.get("error_description").asText() : null;
                                        Map<String, String> details = new HashMap<>();
                                        if (err != null) details.put("error", err);
                                        if (desc != null) details.put("error_description", desc);
                                        if (details.isEmpty())
                                            details.put("message", body.isEmpty() ? "no body" : body);
                                        return Mono.error(new KeycloakTokenException(resp.statusCode(), details, body));
                                    } catch (JsonProcessingException e) {
                                        Map<String, String> details = Map.of("message", body.isEmpty() ? "no body" : body);
                                        return Mono.error(new KeycloakTokenException(resp.statusCode(), details, body));
                                    }
                                });
                    }
                });
    }

    public Mono<TokenResponse> createUserInKeycloak(RegistrationRequest userRequest) {
        return getAdminAccessToken()
                .flatMap(token -> {
                    Map<String, Object> user = Map.of(
                            "username", userRequest.username(),
                            "email", userRequest.email(),
                            "enabled", true,
                            "firstName", userRequest.firstName(),
                            "lastName", userRequest.lastName()
                    );

                    return client().
                            post()
                            .uri("/admin/realms/{realm}/users", realm)
                            .headers(h -> h.setBearerAuth(token.accessToken()))
                            .contentType(MediaType.APPLICATION_JSON) // TODO: urlencoded
                            .bodyValue(user)
                            .exchangeToMono(resp -> {
                                if (resp.statusCode().is2xxSuccessful() || resp.statusCode().equals(HttpStatus.CREATED)) {
                                    return Mono.empty();
                                }
                                return resp.bodyToMono(String.class)
                                        .defaultIfEmpty("")
                                        .flatMap(body -> Mono.error(new RuntimeException("Failed to create user in Keycloak: " + body))); // TODO: custom exception
                            })
                            .then(getUserId(token.accessToken(), userRequest.username()))
                            .flatMap(userId -> {
                                Map<String, Object> credential = Map.of(
                                        "type", "password",
                                        "value", userRequest.password(),
                                        "temporary", false
                                );
                                return client()
                                        .put()
                                        .uri("/admin/realms/{realm}/users/{id}/reset-password", realm, userId)
                                        .headers(h -> h.setBearerAuth(token.accessToken()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(credential)
                                        .retrieve()
                                        .toBodilessEntity()
                                        .flatMap(entity -> login(new LoginRequest(userRequest.username(), userRequest.password()))
                                                .map(map -> new TokenResponse(map.accessToken())));
                            });
                });
    }

    private Mono<String> getUserId(String token, String username) {
        return client()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .queryParam("username", username)
                        .build(realm))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(list -> {
                    if (list != null && !list.isEmpty()) {
                        Map<String, Object> user = list.get(0);
                        return Mono.just((String) user.get("id"));
                    }
                    return Mono.error(new RuntimeException("User not found in Keycloak")); // TODO: custom exception
                });
    }

    private Mono<TokenResponse> getAdminAccessToken() {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("no body")
                                .flatMap(b -> Mono.error(new RuntimeException("Keycloak admin token error: " + b)))) // TODO: custom exception
                .bodyToMono(TokenResponse.class);
    }
}
