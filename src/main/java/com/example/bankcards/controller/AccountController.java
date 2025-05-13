package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Пользовательский аккаунт")
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;

    public AccountController(AccountService service, AccountMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Получить данные аккаунта по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Найдено"),
                    @ApiResponse(responseCode = "404", description = "Пользователя с id не существует", content = @Content)
            })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable int id) {
        return null;
    }

    @Operation(
            summary = "Создать пользователя",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь создан"),
                    @ApiResponse(responseCode = "400", description = "Неправильные данные", content = @Content)
            })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody Account account) {
        return null;
    }

    @Operation(
            summary = "Заблокировать карту",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта заблокирована"),
                    @ApiResponse(responseCode = "404", description = "Карты не существует", content = @Content)
            })
    public ResponseEntity<Card> blockCard(@RequestBody Card card) {
        return null;
    }
}
