package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.pagination.PageCardResponse;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.shared.util.JwtPrincipalUsername;
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
            summary = "Get paginated list of cards",
            description = "ADMIN sees all cards; USER sees only own cards. Supports status pagination",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A page of cards",
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
        String username = JwtPrincipalUsername.getUsername(auth);

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")
        );
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Card> cards = isAdmin
                ? service.getAllCards(pageRequest)
                : service.getByOwner(username, pageRequest);
        Page<CardResponse> dtos = cards.map(isAdmin
                ? mapper::toMaskedResponse
                : mapper::toFullResponse
        );
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get card by ID",
            description = "USER sees full if owner, else masked.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card found",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    )
            }
    )
    public ResponseEntity<CardResponse> getCard(
            @Parameter(description = "ID of the card to retrieve", required = true)
            @PathVariable Long id,
            Authentication auth
    ) {
        Card card = service.getById(id);
        CardResponse dto = service.isOwner(id, auth.getName()) ?
                mapper.toFullResponse(card) :
                mapper.toMaskedResponse(card);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new card",
            description = "Creates a new card for user. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Card successfully created",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))
                    )
            }
    )
    public ResponseEntity<CardResponse> createCard(
            @Parameter(description = "ID of the user for whom the card is created", required = true)
            @RequestParam UUID userId
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
            summary = "Request card block",
            description = "Submits a request to block the specified card. Requires USER role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Block request submitted",
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
                    )
            }
    )
    public ResponseEntity<CardBlockRequest> approveCardBlock(
            @Parameter(description = "ID of the card to approve block for", required = true)
            @PathVariable Long id
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = JwtPrincipalUsername.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.approveBlockRequest(id, UUID.fromString(userId));
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
                    )
            }
    )
    public ResponseEntity<CardBlockRequest> refuseCardBlock(
            @Parameter(description = "ID of the card to reject block for", required = true)
            @PathVariable Long id
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = JwtPrincipalUsername.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.rejectBlockRequest(id, UUID.fromString(userId));
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
                    )
            }
    )
    public ResponseEntity<Void> transfer(
            @Parameter(description = "Card number to transfer from", required = true)
            @RequestBody @Valid TransferRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = JwtPrincipalUsername.getUsername(auth);
        service.transfer(request, username);
        return ResponseEntity.ok().build();
    }
}