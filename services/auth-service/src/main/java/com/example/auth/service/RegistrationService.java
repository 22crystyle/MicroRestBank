package com.example.auth.service;

import com.example.auth.config.KeycloakProperties;
import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegistrationRequest;
import com.example.auth.dto.response.TokenResponse;
import com.example.auth.exception.AdminTokenException;
import com.example.auth.exception.KeycloakTokenException;
import com.example.auth.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final KeycloakProperties keycloakProperties;
    private final ObjectMapper MAPPER = new ObjectMapper();

    private WebClient client() {
        return webClientBuilder.baseUrl(keycloakProperties.serverUrl()).build();
    }

    public Mono<TokenResponse> login(LoginRequest loginRequest) {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", keycloakProperties.clientId())
                        .with("client_secret", keycloakProperties.clientSecret())
                        .with("username", loginRequest.username())
                        .with("password", loginRequest.password()))
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            try {
                                JsonNode node = MAPPER.readTree(body);
                                Map<String, String> details = new HashMap<>();
                                if (node.has("error")) details.put("error", node.get("error").asText());
                                if (node.has("error_description"))
                                    details.put("error_description", node.get("error_description").asText());
                                return Mono.error(new KeycloakTokenException(response.statusCode(), details, body));
                            } catch (JsonProcessingException e) {
                                return Mono.error(new KeycloakTokenException(response.statusCode(), Map.of("message", body), body));
                            }
                        }))
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> createUserInKeycloak(RegistrationRequest userRequest) {
        return getAdminAccessToken()
                .flatMap(token -> createUser(token, userRequest)
                        .then(getUserId(token, userRequest.username()))
                        .flatMap(userId -> resetPassword(token, userId, userRequest.password()))
                        .then(login(new LoginRequest(userRequest.username(), userRequest.password()))));
    }

    private Mono<Void> createUser(TokenResponse token, RegistrationRequest userRequest) {
        Map<String, Object> user = Map.of(
                "username", userRequest.username(),
                "email", userRequest.email(),
                "enabled", true,
                "firstName", userRequest.firstName(),
                "lastName", userRequest.lastName()
        );

        return client().
                post()
                .uri("/admin/realms/{realm}/users", keycloakProperties.realm())
                .headers(h -> h.setBearerAuth(token.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    try {
                                        JsonNode node = MAPPER.readTree(body);
                                        Map<String, String> details = new HashMap<>();
                                        if (node.has("errorMessage")) {
                                            details.put("error_message", node.get("errorMessage").asText());
                                        } else if (node.has("error")) {
                                            details.put("error", node.get("error").asText());
                                        } else {
                                            details.put("error", body);
                                        }
                                        return Mono.error(new KeycloakTokenException(response.statusCode(), details, body));
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new KeycloakTokenException(response.statusCode(), Map.of("message", body), body));
                                    }
                                }))
                .toBodilessEntity()
                .then();
    }

    private Mono<Void> resetPassword(TokenResponse token, String userId, String password) {
        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );
        return client()
                .put()
                .uri("/admin/realms/{realm}/users/{id}/reset-password", keycloakProperties.realm(), userId)
                .headers(h -> h.setBearerAuth(token.accessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(credential)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    private Mono<String> getUserId(TokenResponse token, String username) {
        return client()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .queryParam("username", username)
                        .build(keycloakProperties.realm()))
                .headers(h -> h.setBearerAuth(token.accessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(list -> {
                    if (list != null && !list.isEmpty()) {
                        Map<String, Object> user = list.get(0);
                        return Mono.just((String) user.get("id"));
                    }
                    return Mono.error(new UserNotFoundException(username));
                });
    }

    private Mono<TokenResponse> getAdminAccessToken() {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakProperties.clientId())
                        .with("client_secret", keycloakProperties.clientSecret()))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("no body")
                                .flatMap(b -> Mono.error(new AdminTokenException("Keycloak admin token error: " + b))))
                .bodyToMono(TokenResponse.class);
    }
}
