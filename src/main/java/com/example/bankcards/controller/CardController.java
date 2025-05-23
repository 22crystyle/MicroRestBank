package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.exception.IsNotOwnerException;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Доступ и управление картами аккаунта")
public class CardController {

    private final CardMapper mapper;
    private final CardService service;
    private final CardBlockRequestService cardBlockRequestService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Card> entities = service.getAllCards(PageRequest.of(page, size));
        Page<CardResponse> dtos = entities.map(mapper::toMaskedResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id,
                                                Principal principal) {
        Card card = service.getCard(id);
        CardResponse dto = service.isOwner(id, principal) ?
                mapper.toFullResponse(card) :
                mapper.toMaskedResponse(card);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> createCard(@RequestParam Long userId) {
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

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> transfer(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount,
            Principal principal) {
        if (!service.isOwner(from, principal) || !service.isOwner(to, principal)) {
            throw new IsNotOwnerException("You are not owner of these cards");
        }

        if (service.transfer(from, to, amount)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
