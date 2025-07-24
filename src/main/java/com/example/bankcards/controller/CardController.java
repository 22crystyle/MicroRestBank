package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.pagination.PageCardResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
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
    @Operation(
            summary = "Get paginated list of cards",
            description = "Returns a paginated list of card resources. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A page of cards",
                            content = @Content(schema = @Schema(implementation = PageCardResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", //TODO: check
                            description = "Invalid page or size parameters",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Page<CardResponse>> getCards(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<Card> entities = service.getAllCards(PageRequest.of(page, size));
        Page<CardResponse> dtos = entities.map(mapper::toMaskedResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get card by ID",
            description = "Returns card details for the specified card ID. Full details are returned if the authenticated user is the owner; otherwise, masked details are provided.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card found",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card ID",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<CardResponse> getCard(
            @Parameter(description = "ID of the card to retrieve", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Card card = service.getCard(id);
        CardResponse dto = service.isOwner(id, userDetails.getUsername()) ?
                mapper.toFullResponse(card) :
                mapper.toMaskedResponse(card);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new card",
            description = "Creates a new card for the specified user ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Card successfully created",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid user ID",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<CardResponse> createCard(
            @Parameter(description = "ID of the user for whom the card is created", required = true)
            @RequestParam Long userId
    ) {
        CardResponse response = mapper.toMaskedResponse(service.createCardForAccount(userId));
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
            summary = "Request card block",
            description = "Submits a request to block the specified card. Requires USER role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Block request submitted",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card ID",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<CardResponse> requestCardBlock(
            @Parameter(description = "ID of the card to block", required = true)
            @PathVariable Long id
    ) {
        cardBlockRequestService.createBlockRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/block-approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve card block request",
            description = "Approves a block request for the specified card. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Block request approved",
                            content = @Content(schema = @Schema(implementation = CardBlockRequest.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card ID",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card or block request not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<CardBlockRequest> approveCardBlock(
            @Parameter(description = "ID of the card to approve block for", required = true)
            @PathVariable Long id,
            @Parameter(description = "Authenticated admin details", hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CardBlockRequest blockRequest = cardBlockRequestService.approveBlockRequest(id, userDetails.getAccountId());
        return ResponseEntity.ok(blockRequest);
    }

    @PostMapping("/{id}/block-reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject card block request",
            description = "Rejects a block request for the specified card. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Block request rejected",
                            content = @Content(schema = @Schema(implementation = CardBlockRequest.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card ID",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card or block request not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<CardBlockRequest> refuseCardBlock(
            @Parameter(description = "ID of the card to reject block for", required = true)
            @PathVariable Long id,
            @Parameter(description = "Authenticated admin details", hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CardBlockRequest blockRequest = cardBlockRequestService.rejectBlockRequest(id, userDetails.getAccountId());
        return ResponseEntity.ok(blockRequest);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Transfer money between cards",
            description = "Transfers the specified amount from one card to another. Both cards must belong to the authenticated user. Requires USER role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transfer successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card numbers or amount",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "One or both cards not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Insufficient funds or invalid transfer",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Void> transfer(
            @Parameter(description = "Card number to transfer from", required = true)
            @RequestParam String fromCard,
            @Parameter(description = "Card number to transfer to", required = true)
            @RequestParam String toCard,
            @Parameter(description = "Amount to transfer", required = true)
            @RequestParam BigDecimal amount,
            @Parameter(description = "Authenticated user principal", hidden = true)
            Principal principal
    ) {
        service.transfer(fromCard, toCard, amount, principal);
        return ResponseEntity.ok().build();
    }
}