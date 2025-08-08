package com.example.auth.controller;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegistrationRequest;
import com.example.auth.dto.response.AuthenticationResponse;
import com.example.auth.service.RegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login endpoint")
public class AuthenticationController {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest request) {
        AuthenticationResponse response = registrationService.createUserInKeycloak(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        String token = registrationService.login(request);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
