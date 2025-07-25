package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.pagination.PageAccountResponse;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.service.CardService;
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
@RequestMapping("/api/v1/accounts")
@PreAuthorize(value = "hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Accounts", description = "Account access and management. requires ADMIN role")
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;
    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping
    @Operation(
            summary = "Get paginated list of accounts",
            description = "Returns a paginated list of account resources, requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A page of accounts",
                            content = @Content(schema = @Schema(implementation = PageAccountResponse.class))
                    )
            }
    )
    public ResponseEntity<Page<AccountResponse>> getPageOfAccounts(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<Account> entities = service.getPage(PageRequest.of(page, size));
        Page<AccountResponse> dtos = entities.map(mapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get account by ID",
            description = "Returns account details for the specified account ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account found",
                            content = @Content(schema = @Schema(implementation = AccountResponse.class))
                    )
            }
    )
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "ID of the account to retrieve", required = true)
            @PathVariable Long id
    ) {
        Account account = service.getAccountById(id);
        AccountResponse response = mapper.toResponse(account);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/cards")
    @Operation(
            summary = "Get all cards by account ID",
            description = "Returns a list of cards associated with the specified account ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of cards",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponse.class)))
                    )
            }
    )
    public ResponseEntity<List<CardResponse>> getAccountCardsByUserId(
            @Parameter(description = "ID of the account", required = true)
            @PathVariable Long id
    ) {
        List<Card> cards = cardService.getCardsByUserId(id);
        List<CardResponse> dtos = cards.stream()
                .map(cardMapper::toMaskedResponse)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(
            summary = "Create new account",
            description = "Receives account data for a new user, saves it to the database, and returns the created object with status code 201 and a Location header.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Account successfully created",
                            content = @Content(schema = @Schema(implementation = AccountResponse.class))
                    )
            }
    )
    public ResponseEntity<AccountResponse> createAccount(
            @Parameter(description = "Account data to create", required = true)
            @RequestBody @Valid AccountRequest request
    ) {
        Account entity = service.createAccount(request);
        AccountResponse response = mapper.toResponse(entity);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete account by id",
            description = "Deletes the account with the specified ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Account successfully deleted",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "ID of the account to delete", required = true)
            @PathVariable Long id
    ) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
