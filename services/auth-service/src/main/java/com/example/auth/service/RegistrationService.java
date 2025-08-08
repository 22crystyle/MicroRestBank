package com.example.auth.service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegistrationRequest;
import com.example.auth.dto.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String login(LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", loginRequest.username());
        form.add("password", loginRequest.password());
        log.info(form.toString());

        HttpEntity<MultiValueMap<String, String>> keycloakRequest = new HttpEntity<>(form, headers);
        Map<?, ?> response = restTemplate.postForObject(
                serverUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                keycloakRequest,
                Map.class
        );

        assert response != null;
        return (String) response.get("access_token");
    }

    public AuthenticationResponse createUserInKeycloak(RegistrationRequest userRequest) {
        String token = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = Map.of(
                "username", userRequest.username(),
                "email", userRequest.email(),
                "enabled", true,
                "firstName", userRequest.firstName(),
                "lastName", userRequest.lastName()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);
        ResponseEntity<Void> resp = restTemplate.postForEntity(
                serverUrl + "/admin/realms/" + realm + "/users/", entity, Void.class
        );

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user in Keycloak");
        }

        String userId = getUserId(token, userRequest.username());

        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", userRequest.password(),
                "temporary", false
        );

        HttpEntity<Map<String, Object>> passEntity = new HttpEntity<>(credential, headers);
        restTemplate.put(serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password", passEntity);

        return new AuthenticationResponse(login(new LoginRequest(userRequest.username(), userRequest.password())));
    }

    private String getUserId(String token, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                serverUrl + "/admin/realms/" + realm + "/users?username=" + username,
                HttpMethod.GET,
                entity,
                List.class
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            Map<?, ?> user = (Map<?, ?>) response.getBody().getFirst();
            return (String) user.get("id");
        }
        throw new RuntimeException("User not found in keycloak");
    }

    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        Map<?, ?> response = restTemplate.postForObject(
                serverUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class
        );
        assert response != null;
        return (String) response.get("access_token");
    }
}
