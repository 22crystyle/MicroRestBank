package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.CardMapperImpl;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AccountService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@PreAuthorize(value = "hasRole('ADMIN')")
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;
    private final CardService cardService;
    private final CardMapperImpl cardMapper;

    public AccountController(AccountService service,
                             AccountMapper mapper,
                             CardService cardService,
                             CardMapperImpl cardMapper) {
        this.service = service;
        this.mapper = mapper;
        this.cardService = cardService;
        this.cardMapper = cardMapper;
    }

    @GetMapping
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable int id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        Account entity = service.createAccount(request);
        AccountResponse response = mapper.toResponse(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardResponse>> getAccountCardsByUserId(@PathVariable Long id) {
        List<Card> cards = cardService.getCardsByUserId(id);
        List<CardResponse> dtos = cards.stream()
                .map(cardMapper::toResponse)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
