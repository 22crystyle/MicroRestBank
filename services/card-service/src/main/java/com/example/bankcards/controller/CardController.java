package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.pagination.PageCardResponse;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.shared.util.JwtPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Access and management of user cards")
@Slf4j
public class CardController {

    private final CardMapper mapper;
    private final CardService service;
    private final CardBlockRequestService cardBlockRequestService;

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Get a paginated list of cards",
            description = "Retrieves a list of cards. Admins can see all cards, while users can only see their own. The card details for admins are masked.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A paginated list of cards.",
                            content = @Content(schema = @Schema(implementation = PageCardResponse.class))
                    )
            }
    )
    public ResponseEntity<Page<CardResponse>> getCards(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = JwtPrincipal.getId(auth);

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")
        );
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Card> cards;
        if (isAdmin) {
            cards = service.getAllCards(pageRequest);
        } else {
            cards = service.getCardsByOwner(UUID.fromString(userId), pageRequest);
        }
        Page<CardResponse> dtos = cards.map(isAdmin
                ? mapper::toMaskedResponse
                : mapper::toFullResponse
        );
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get a card by its ID",
            description = "Retrieves a single card's details. A user sees the full card details if they are the owner, otherwise the details are masked.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The card details.",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Card not found.")
            }
    )
    public ResponseEntity<CardResponse> getCard(
            @Parameter(description = "ID of the card to retrieve", required = true)
            @PathVariable Long id,
            Authentication auth
    ) {
        Card card = service.getById(id);
        CardResponse dto = service.checkOwnership(id, UUID.fromString(JwtPrincipal.getId(auth))) ?
                mapper.toFullResponse(card) :
                mapper.toMaskedResponse(card);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new card for a user",
            description = "Creates a new bank card for a specified user. This action requires administrator privileges.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Card created successfully. The response contains the details of the created card.",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    )
            }
    )
    public ResponseEntity<CardResponse> createCard(
            @Parameter(description = "ID of the user for whom the card is created", required = true)
            @RequestParam("userId") UUID userId
    ) {
        Card card = service.createCardForAccount(userId);
        CardResponse response = mapper.toMaskedResponse(card);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{id}/block-request")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Request to block a card",
            description = "Allows a user to submit a request to block their own card.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card block request submitted successfully."
                    ),
                    @ApiResponse(responseCode = "404", description = "Card not found."),
                    @ApiResponse(responseCode = "403", description = "User is not the owner of the card.")
            }
    )
    public ResponseEntity<Void> requestCardBlock(
            @Parameter(description = "ID of the card to block", required = true)
            @PathVariable Long id
    ) {
        log.info("Trying to send request to block card with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(JwtPrincipal.getId(auth));
        cardBlockRequestService.createBlockRequest(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/block-approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve a card block request",
            description = "Allows an administrator to approve a request to block a card.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The card block request was approved successfully.",
                            content = @Content(schema = @Schema(implementation = CardBlockRequest.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Block request not found or already processed.")
            }
    )
    public ResponseEntity<CardBlockRequest> approveCardBlock(
            @Parameter(description = "ID of the card to approve block for", required = true)
            @PathVariable Long id
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = JwtPrincipal.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.approveBlockRequest(id, UUID.fromString(userId));
        return ResponseEntity.ok(blockRequest);
    }

    @PostMapping("/{id}/block-reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject a card block request",
            description = "Allows an administrator to reject a request to block a card.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The card block request was rejected successfully.",
                            content = @Content(schema = @Schema(implementation = CardBlockRequest.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Block request not found or already processed.")
            }
    )
    public ResponseEntity<CardBlockRequest> refuseCardBlock(
            @Parameter(description = "ID of the card to reject block for", required = true)
            @PathVariable Long id
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = JwtPrincipal.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.rejectBlockRequest(id, UUID.fromString(userId));
        return ResponseEntity.ok(blockRequest);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Transfer money between two cards",
            description = "Allows a user to transfer a specified amount of money from one of their cards to another.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The transfer was completed successfully."
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid transfer request (e.g., insufficient funds, invalid card numbers)."),
                    @ApiResponse(responseCode = "403", description = "User does not own one or both of the cards.")
            }
    )
    public ResponseEntity<Void> transfer(
            @Parameter(description = "The details of the transfer, including source and destination card numbers and the amount.", required = true)
            @RequestBody @Valid TransferRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String id = JwtPrincipal.getId(auth);
        service.transfer(request, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
