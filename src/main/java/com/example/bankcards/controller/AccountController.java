package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
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
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;
    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping
    public ResponseEntity<Page<AccountResponse>> getAccounts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Max(100) int size
    ) {
        Page<Account> entities = service.getAllAccounts(PageRequest.of(page, size));
        Page<AccountResponse> dtos = entities.map(mapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "Get account by ID",
            description = "Returns account details for the specified account ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account found"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Account not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "ID of the account to retrieve", required = true)
            @PathVariable Long id) {
        Account account = service.getAccountById(id);
        AccountResponse response = mapper.toResponse(account);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardResponse>> getAccountCardsByUserId(@PathVariable Long id) {
        List<Card> cards = cardService.getCardsByUserId(id);
        List<CardResponse> dtos = cards.stream()
                .map(cardMapper::toMaskedResponse)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest request) {
        Account entity = service.createAccount(request);
        AccountResponse response = mapper.toResponse(entity);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    //  TODO: удалить сперва все карты пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
