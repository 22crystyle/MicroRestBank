package org.restbank.platform.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restbank.platform.auth.config.KeycloakProperties;
import org.restbank.platform.auth.dto.request.LoginRequest;
import org.restbank.platform.auth.dto.request.RegistrationRequest;
import org.restbank.platform.auth.dto.response.TokenResponse;
import org.restbank.platform.auth.exception.AdminTokenException;
import org.restbank.platform.auth.exception.KeycloakTokenException;
import org.restbank.platform.auth.exception.UserNotFoundException;
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

/**
 * Service for handling user registration and login with Keycloak.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    private static final String GRANT_TYPE = "grant_type";
    private static final String PASSWORD = "password";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String USERNAME = "username";

    private static final String ERROR = "error";
    private static final String ERROR_DESCRIPTION = "error_description";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ID = "id";

    private final WebClient.Builder webClientBuilder;
    private final KeycloakProperties keycloakProperties;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a WebClient instance for communicating with Keycloak.
     *
     * @return A configured {@link WebClient} instance.
     */
    private WebClient client() {
        return webClientBuilder.baseUrl(keycloakProperties.serverUrl()).build();
    }

    /**
     * Authenticates a user with Keycloak and returns an access token.
     *
     * @param loginRequest The user's login credentials.
     * @return A {@link Mono} containing a {@link TokenResponse} with the access token.
     * @throws KeycloakTokenException if authentication fails.
     */
    public Mono<TokenResponse> login(LoginRequest loginRequest) {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData(GRANT_TYPE, PASSWORD)
                        .with(CLIENT_ID, keycloakProperties.clientId())
                        .with(CLIENT_SECRET, keycloakProperties.clientSecret())
                        .with(USERNAME, loginRequest.username())
                        .with(PASSWORD, loginRequest.password()))
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            try {
                                JsonNode node = mapper.readTree(body);
                                Map<String, String> details = new HashMap<>();
                                if (node.has(ERROR)) details.put(ERROR, node.get(ERROR).asText());
                                if (node.has(ERROR_DESCRIPTION))
                                    details.put(ERROR_DESCRIPTION, node.get(ERROR_DESCRIPTION).asText());
                                return Mono.error(new KeycloakTokenException(response.statusCode(), details, body));
                            } catch (JsonProcessingException e) {
                                return Mono.error(new KeycloakTokenException(response.statusCode(), Map.of("message", body), body));
                            }
                        }))
                .bodyToMono(TokenResponse.class);
    }

    /**
     * Creates a new user in Keycloak and returns an access token.
     *
     * <p>This method orchestrates the user creation process by:
     * <ol>
     *     <li>Obtaining an admin access token.</li>
     *     <li>Creating the user in Keycloak.</li>
     *     <li>Retrieving the new user's ID.</li>
     *     <li>Setting the user's password.</li>
     *     <li>Logging in as the new user to get an access token.</li>
     * </ol>
     *
     * @param userRequest The registration details for the new user.
     * @return A {@link Mono} containing a {@link TokenResponse} with the access token.
     */
    public Mono<TokenResponse> createUserInKeycloak(RegistrationRequest userRequest) {
        return getAdminAccessToken()
                .flatMap(token -> createUser(token, userRequest)
                        .then(getUserId(token, userRequest.username()))
                        .flatMap(userId -> resetPassword(token, userId, userRequest.password()))
                        .then(login(new LoginRequest(userRequest.username(), userRequest.password()))));
    }

    /**
     * Creates a user in Keycloak using an admin access token.
     *
     * @param token       The admin access token.
     * @param userRequest The registration details for the new user.
     * @return A {@link Mono} that completes when the user is created.
     */
    private Mono<Void> createUser(TokenResponse token, RegistrationRequest userRequest) {
        Map<String, Object> user = Map.of(
                USERNAME, userRequest.username(),
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
                                        JsonNode node = mapper.readTree(body);
                                        Map<String, String> details = new HashMap<>();
                                        if (node.has(ERROR_MESSAGE)) {
                                            details.put("error_message", node.get(ERROR_MESSAGE).asText());
                                        } else if (node.has(ERROR)) {
                                            details.put(ERROR, node.get(ERROR).asText());
                                        } else {
                                            details.put(ERROR, body);
                                        }
                                        return Mono.error(new KeycloakTokenException(response.statusCode(), details, body));
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new KeycloakTokenException(response.statusCode(), Map.of("message", body), body));
                                    }
                                }))
                .toBodilessEntity()
                .then();
    }

    /**
     * Sets the password for a user in Keycloak.
     *
     * @param token    The admin access token.
     * @param userId   The ID of the user.
     * @param password The new password for the user.
     * @return A {@link Mono} that completes when the password is set.
     */
    private Mono<Void> resetPassword(TokenResponse token, String userId, String password) {
        Map<String, Object> credential = Map.of(
                "type", PASSWORD,
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

    /**
     * Retrieves the ID of a user from Keycloak by their username.
     *
     * @param token    The admin access token.
     * @param username The username of the user to find.
     * @return A {@link Mono} containing the user's ID.
     * @throws UserNotFoundException if the user is not found.
     */
    private Mono<String> getUserId(TokenResponse token, String username) {
        return client()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .queryParam(USERNAME, username)
                        .build(keycloakProperties.realm()))
                .headers(h -> h.setBearerAuth(token.accessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(list -> {
                    if (list != null && !list.isEmpty()) {
                        Map<String, Object> user = list.getFirst();
                        return Mono.just((String) user.get(ID));
                    }
                    return Mono.error(new UserNotFoundException(username));
                });
    }

    /**
     * Obtains an admin access token from Keycloak using client credentials.
     *
     * @return A {@link Mono} containing a {@link TokenResponse} with the admin access token.
     * @throws AdminTokenException if obtaining the token fails.
     */
    private Mono<TokenResponse> getAdminAccessToken() {
        return client()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData(GRANT_TYPE, "client_credentials")
                        .with(CLIENT_ID, keycloakProperties.clientId())
                        .with(CLIENT_SECRET, keycloakProperties.clientSecret()))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("no body")
                                .flatMap(b -> Mono.error(new AdminTokenException("Keycloak admin token error: " + b))))
                .bodyToMono(TokenResponse.class);
    }
}
