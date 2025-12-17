package org.restbank.service.card.controller;

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
import org.restbank.libs.api.util.JwtPrincipal;
import org.restbank.service.card.dto.request.TransferRequest;
import org.restbank.service.card.dto.response.CardResponse;
import org.restbank.service.card.entity.CardBlockRequest;
import org.restbank.service.card.repository.CardBlockRequestRepository;
import org.restbank.service.card.service.CardBlockRequestService;
import org.restbank.service.card.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing bank cards.
 *
 * <p>This controller provides endpoints for creating, retrieving, and managing bank cards.
 * It includes operations for listing cards, viewing card details, creating new cards, requesting
 * and processing card blocks, and transferring funds between cards. Access to these endpoints
 * is protected by role-based security.</p>
 */
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Access and management of user cards")
@Slf4j
public class CardController {

    private final CardService service;
    private final CardBlockRequestService cardBlockRequestService;
    private final PagedResourcesAssembler<CardResponse> assembler;
    private final CardBlockRequestRepository cardBlockRequestRepository;

    /**
     * Retrieves a paginated list of cards.
     *
     * <p>This endpoint is accessible to both 'ADMIN' and 'USER' roles. Administrators receive a list
     * of all cards with masked details, while users can only see their own cards with full details.
     * The response includes HATEOAS links for navigation and related actions.</p>
     *
     * @param page The page number to retrieve (0-based).
     * @param size The number of cards per page.
     * @param auth The current authentication object, used to determine the user's roles and identity.
     * @return A {@link ResponseEntity} containing a {@link PagedModel} of {@link CardResponse} objects.
     */
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
                            content = @Content(schema = @Schema(implementation = PagedModel.class))
                    )
            }
    )
    public ResponseEntity<PagedModel<EntityModel<CardResponse>>> getCards(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            Authentication auth
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CardResponse> dtos = service.getCards(pageRequest, auth);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        return ResponseEntity.ok(assembler.toModel(dtos, card -> {
            EntityModel<CardResponse> model = EntityModel.of(card,
                    linkTo(methodOn(CardController.class).getCard(card.getId(), auth)).withSelfRel());

            if (isAdmin && cardBlockRequestRepository.existsCardBlockRequestByCard_IdAndStatus(card.getId(), CardBlockRequest.Status.PENDING)) {
                model.add(linkTo(methodOn(CardController.class).approveCardBlock(card.getId(), auth)).withRel("block-approve"));
                model.add(linkTo(methodOn(CardController.class).refuseCardBlock(card.getId(), auth)).withRel("block-reject"));
            }
            return model;
        }));
    }

    /**
     * Retrieves the details of a specific card by its ID.
     *
     * <p>This endpoint is accessible to any authenticated user. If the user is the owner of the card,
     * the full card details are returned. If the user is an administrator, the details are masked.
     * Otherwise, access denied error is thrown.</p>
     *
     * @param id   The ID of the card to retrieve.
     * @param auth The current authentication object.
     * @return A {@link ResponseEntity} containing an {@link EntityModel} of the {@link CardResponse}.
     */
    @SecurityRequirement(name = "BearerAuth")
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
    public ResponseEntity<EntityModel<CardResponse>> getCard(
            @Parameter(description = "ID of the card to retrieve", required = true)
            @PathVariable Long id,
            Authentication auth
    ) {
        CardResponse dto = service.getCard(id, auth);
        EntityModel<CardResponse> model = EntityModel.of(dto,
                linkTo(methodOn(CardController.class).getCard(id, auth)).withSelfRel(),
                linkTo(CardController.class).withRel("cards"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        if (isAdmin && cardBlockRequestRepository.existsCardBlockRequestByCard_IdAndStatus(id, CardBlockRequest.Status.PENDING)) {
            model.add(linkTo(methodOn(CardController.class).approveCardBlock(id, auth)).withRel("block-approve"));
            model.add(linkTo(methodOn(CardController.class).refuseCardBlock(id, auth)).withRel("block-reject"));
        }

        return ResponseEntity.ok(model);
    }

    /**
     * Creates a new bank card for a specified user.
     *
     * <p>This endpoint is restricted to users with the 'ADMIN' role. It creates a new card
     * for the user identified by the given UUID and returns the details of the created card
     * with a masked PAN.</p>
     *
     * @param userId The UUID of the user for whom the card is being created.
     * @param auth   The current authentication object.
     * @return A {@link ResponseEntity} with status 201 (Created) and the created {@link CardResponse}.
     */
    @SecurityRequirement(name = "BearerAuth")
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
    public ResponseEntity<EntityModel<CardResponse>> createCard(
            @Parameter(description = "ID of the user for whom the card is created", required = true)
            @RequestParam("userId") UUID userId,
            Authentication auth
    ) {
        CardResponse response = service.createCardForAccount(userId);
        EntityModel<CardResponse> model = EntityModel.of(response,
                linkTo(methodOn(CardController.class).getCard(response.getId(), auth)).withSelfRel(),
                linkTo(methodOn(CardController.class).requestCardBlock(response.getId(), auth)).withRel("block-request")
        );

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    /**
     * Submits a request to block a card.
     *
     * <p>This endpoint allows a user with the 'USER' role to request that their own card be blocked.
     * The request is then processed by an administrator.</p>
     *
     * @param id   The ID of the card to be blocked.
     * @param auth The current authentication object.
     * @return A {@link ResponseEntity} with status 200 (OK) if the request is submitted successfully.
     */
    @SecurityRequirement(name = "BearerAuth")
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
            @PathVariable Long id,
            Authentication auth
    ) {
        log.info("Trying to send request to block card with id: {}", id);
        UUID userId = UUID.fromString(JwtPrincipal.getId(auth));
        cardBlockRequestService.createBlockRequest(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Approves a pending card block request.
     *
     * <p>This endpoint is restricted to users with the 'ADMIN' role. It approves a block request
     * for the specified card, changing the card's status to 'BLOCKED'.</p>
     *
     * @param id   The ID of the card for which the block request is being approved.
     * @param auth The current authentication object.
     * @return A {@link ResponseEntity} containing the updated {@link CardBlockRequest}.
     */
    @SecurityRequirement(name = "BearerAuth")
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
            @PathVariable Long id,
            Authentication auth
    ) {
        String userId = JwtPrincipal.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.approveBlockRequest(id, UUID.fromString(userId));
        return ResponseEntity.ok(blockRequest);
    }

    /**
     * Rejects a pending card block request.
     *
     * <p>This endpoint is restricted to users with the 'ADMIN' role. It rejects a block request
     * for the specified card, and the card's status remains unchanged.</p>
     *
     * @param id   The ID of the card for which the block request is being rejected.
     * @param auth The current authentication object.
     * @return A {@link ResponseEntity} containing the updated {@link CardBlockRequest}.
     */
    @SecurityRequirement(name = "BearerAuth")
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
            @PathVariable Long id,
            Authentication auth
    ) {
        String userId = JwtPrincipal.getId(auth);
        CardBlockRequest blockRequest = cardBlockRequestService.rejectBlockRequest(id, UUID.fromString(userId));
        return ResponseEntity.ok(blockRequest);
    }

    /**
     * Transfers funds between two cards.
     *
     * <p>This endpoint allows a user with the 'USER' role to transfer a specified amount of money
     * from one of their cards to another. The user must be the owner of both cards.</p>
     *
     * @param request The {@link TransferRequest} containing the source and destination card IDs and the amount.
     * @param auth    The current authentication object.
     * @return A {@link ResponseEntity} with status 200 (OK) if the transfer is successful.
     */
    @SecurityRequirement(name = "BearerAuth")
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
            @RequestBody @Valid TransferRequest request,
            Authentication auth
    ) {
        String id = JwtPrincipal.getId(auth);
        service.transfer(request, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
