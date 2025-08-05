package com.example.bankcards.controller;

import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.shared.dto.CardMapper;
import com.example.shared.dto.UserMapper;
import com.example.shared.dto.pagination.PageOfUserResponse;
import com.example.shared.dto.request.UserRequest;
import com.example.shared.dto.response.CardResponse;
import com.example.shared.dto.response.UserResponse;
import com.example.shared.entity.Card;
import com.example.shared.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize(value = "hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "User access and management. requires ADMIN role")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;
    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping
    @Operation(
            summary = "Get paginated list of users",
            description = "Returns a paginated list of user resources, requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A page of users",
                            content = @Content(schema = @Schema(implementation = PageOfUserResponse.class))
                    )
            }
    )
    public ResponseEntity<Page<UserResponse>> getPageOfUsers(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<User> entities = service.getPage(PageRequest.of(page, size));
        Page<UserResponse> dtos = entities.map(mapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Returns user details for the specified user ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    )
            }
    )
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id
    ) {
        User user = service.getUserById(id);
        UserResponse response = mapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/cards")
    @Operation(
            summary = "Get all cards by user ID",
            description = "Returns a list of cards associated with the specified user ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of cards",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponse.class)))
                    )
            }
    )
    public ResponseEntity<List<CardResponse>> getUserCardsByUserId(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long id
    ) {
        List<Card> cards = cardService.getByOwner(id);
        List<CardResponse> dtos = cards.stream()
                .map(cardMapper::toMaskedResponse)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(
            summary = "Create new user",
            description = "Receives user data for a new user, saves it to the database, and returns the created object with status code 201 and a location header.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    )
            }
    )
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "User data to create", required = true)
            @RequestBody @Valid UserRequest request
    ) {
        User entity = service.createUser(request);
        UserResponse response = mapper.toResponse(entity);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by id",
            description = "Deletes the user with the specified ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User successfully deleted",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id
    ) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
