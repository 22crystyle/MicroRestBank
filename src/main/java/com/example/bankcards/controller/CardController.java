package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/cards")
@Tag(name = "Cards", description = "Доступ и управление картами аккаунта")
public class CardController {

    private final CardMapper cardMapper;
    private final CardService cardService;

    public CardController(CardMapper cardMapper, CardService cardService) {
        this.cardMapper = cardMapper;
        this.cardService = cardService;
    }

    @Operation(
            summary = "Получение карт прикрепленных к пользователю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Получение карт"),
                    @ApiResponse(responseCode = "404", description = "Карты не найдены", content = @Content)
            })
    @GetMapping
    public EntityResponse<List<CardResponse>> getCards(
            @PathVariable("accountId")
            @Parameter(name = "accountId",  description = "Id пользователя", example = "1")
            int accountId) {
        return null;
    }

    @Operation(
            summary = "Создание карты",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Карта создана"),
                    @ApiResponse(responseCode = "400", description = "Неправильные данные", content = @Content)
            })
    @PostMapping
    public EntityResponse<CardResponse> createCard(
            @PathVariable("accountId")
            @Parameter(name = "accountId", description = "Id пользователя", example = "1")
            int accountId, @RequestBody @Valid CardRequest cardRequest) {
        return null;
    }

    @Operation (
            summary = "Перевод между счетами",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный перевод"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные")
            }
    )
    @PutMapping("/{id}")
    public EntityResponse<CardResponse> transfer(int accountId, @PathVariable("id") int id) {
        return null;
    }
}
