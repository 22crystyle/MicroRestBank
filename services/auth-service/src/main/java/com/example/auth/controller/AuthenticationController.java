package com.example.auth.controller;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.service.RegistrationService;
import com.example.shared.dto.UserMapper;
import com.example.shared.dto.request.UserRequest;
import com.example.shared.dto.response.UserResponse;
import com.example.shared.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login endpoint")
public class AuthenticationController {
    private final RegistrationService registrationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        User user = registrationService.register(request);
        UserResponse dto = userMapper.toResponse(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = registrationService.login(request);
        return ResponseEntity.ok(Map.of("access_token", token));
    }
}
