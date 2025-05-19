package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Доступ и управление картами аккаунта")
public class CardController {

    private final CardMapper cardMapper;
    private final CardService cardService;

    public CardController(CardMapper cardMapper,
                          CardService cardService) {
        this.cardMapper = cardMapper;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getCards(@AuthenticationPrincipal CustomUserDetails user) {
        return null;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long accountId = userDetails.getAccountId();

        CardResponse response = cardMapper.toResponse(cardService.createCardForAccount(accountId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/block-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> requestCardBlock(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/{id}/block-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> confirmCardBlock(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {
        return null;
    }

    @PostMapping("/{id}/block-reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> refuseCardBlock(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {
        return null;
    }

    @PutMapping("/transfer")
    public ResponseEntity<CardResponse> transfer(@RequestBody TransferRequest request) {
        return null;
    }
}
