package com.example.auth.controller;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegistrationRequest;
import com.example.auth.dto.response.TokenResponse;
import com.example.auth.service.RegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login endpoint")
public class AuthenticationController {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<Mono<TokenResponse>> register(@RequestBody RegistrationRequest request) {
        Mono<TokenResponse> response = registrationService.createUserInKeycloak(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Mono<TokenResponse>> login(@RequestBody LoginRequest request) {
        Mono<TokenResponse> token = registrationService.login(request);
        return ResponseEntity.ok(token);
    }
}
