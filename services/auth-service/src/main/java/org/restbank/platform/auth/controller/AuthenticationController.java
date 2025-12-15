package org.restbank.platform.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restbank.platform.auth.dto.request.LoginRequest;
import org.restbank.platform.auth.dto.request.RegistrationRequest;
import org.restbank.platform.auth.dto.response.TokenResponse;
import org.restbank.platform.auth.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthenticationController {
    private final RegistrationService registrationService;

    @Operation(summary = "Register a new user",
            description = "Creates a new user in Keycloak and returns an access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration request", content = @Content),
            @ApiResponse(responseCode = "409", description = "User with the same username or email already exists", content = @Content)
    })
    @PostMapping("/register")
    public Mono<ResponseEntity<TokenResponse>> register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.createUserInKeycloak(request)
                .map(token -> ResponseEntity.status(HttpStatus.CREATED).body(token));
    }

    @Operation(summary = "User login",
            description = "Authenticates a user and returns an access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginRequest request) {
        return registrationService.login(request)
                .map(ResponseEntity::ok);
    }

}
