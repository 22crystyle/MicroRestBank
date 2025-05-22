package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Доступ и управление картами аккаунта")
public class CardController {

    private final CardMapper mapper;
    private final CardService service;
    private final CardBlockRequestService cardBlockRequestService;

    public CardController(CardMapper mapper,
                          CardService service, CardBlockRequestService cardBlockRequestService) {
        this.mapper = mapper;
        this.service = service;
        this.cardBlockRequestService = cardBlockRequestService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id, Principal principal) {
        Card card = service.getCard(id);
        CardResponse cardResponse;
        if (!service.isOwner(id, principal)) {
            cardResponse = mapper.toMaskedResponse(card);
        }
        else {
            cardResponse = mapper.toFullResponse(card);
        }
        return ResponseEntity.ok(cardResponse);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @RequestParam Long userId
    ) {
        CardResponse response = mapper.toMaskedResponse(service.createCardForAccount(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/block-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> requestCardBlock(@PathVariable Long id) {
        cardBlockRequestService.createBlockRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/block-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardBlockRequest> approveCardBlock(@PathVariable Long id,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        CardBlockRequest blockRequest = cardBlockRequestService.approveBlockRequest(id, userDetails.getAccountId());
        return ResponseEntity.ok(blockRequest);
    }

    @PostMapping("/{id}/block-reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardBlockRequest> refuseCardBlock(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CardBlockRequest blockRequest = cardBlockRequestService.rejectBlockRequest(id, userDetails.getAccountId());
        return ResponseEntity.ok(blockRequest);
    }

    @PutMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> transfer(@RequestBody TransferRequest request) {
        return null;
    }
}
