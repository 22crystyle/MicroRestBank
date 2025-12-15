package org.restbank.service.card.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restbank.service.card.entity.Card;
import org.restbank.service.card.entity.CardBlockRequest;
import org.restbank.service.card.entity.CardStatusType;
import org.restbank.service.card.exception.CardNotFoundException;
import org.restbank.service.card.exception.IsNotOwnerException;
import org.restbank.service.card.repository.CardBlockRequestRepository;
import org.restbank.service.card.repository.CardRepository;
import org.restbank.service.card.repository.CardStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for managing card block requests.
 * Handles creation, approval, and rejection of requests to block bank cards.
 */

/**
 * Service for managing card block requests.
 * Handles creation, approval, and rejection of requests to block bank cards.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardBlockRequestService {

    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;

    /**
     * Creates a new card block request for a given card and user.
     *
     * @param cardId The ID of the card to be blocked.
     * @param userId The ID of the user initiating the block request.
     * @throws CardNotFoundException if the card with the given ID is not found.
     * @throws IsNotOwnerException if the user is not the owner of the card.
     * @throws IllegalArgumentException if a pending block request already exists for the card.
     */
    /**
     * Creates a new card block request for a given card and user.
     *
     * @param cardId The ID of the card to be blocked.
     * @param userId The ID of the user initiating the block request.
     * @throws CardNotFoundException    if the card with the given ID is not found.
     * @throws IsNotOwnerException      if the user is not the owner of the card.
     * @throws IllegalArgumentException if a pending block request already exists for the card.
     */
    @Transactional
    public void createBlockRequest(Long cardId, UUID userId) {
        log.debug("createBlockRequest called with cardId={} by userId={}", cardId, userId); // TODO: вынести в aop
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        UUID ownerId = card.getUser().getId();
        if (!ownerId.equals(userId)) {
            log.warn("User {} tried to block card {} but is not owner (ownerId={})", userId, cardId, ownerId);
            throw new IsNotOwnerException("You are not owner of card");
        }

        if (cardBlockRequestRepository.existsCardBlockRequestByCard_IdAndStatus(cardId, CardBlockRequest.Status.PENDING)) {
            log.warn("Duplicate block request attempt for cardId={} by userId={}", cardId, userId);
            throw new IllegalArgumentException("Card block request already exists");
        }

        CardBlockRequest blockRequest = CardBlockRequest.builder()
                .card(card)
                .createdAt(Instant.now())
                .status(CardBlockRequest.Status.PENDING)
                .processedBy(null)
                .processedAt(null)
                .build();

        cardBlockRequestRepository.save(blockRequest);
        log.info("Created block request for cardId={} by userId={}", cardId, userId);
    }

    /**
     * Approves a pending card block request.
     *
     * @param cardId      The ID of the card for which the block request is to be approved.
     * @param processedBy The ID of the user who processed the approval.
     * @return The updated CardBlockRequest entity.
     * @throws IllegalArgumentException if a pending block request for the card is not found.
     * @throws IllegalArgumentException if the card status 'BLOCKED' is not found.
     */
    @Transactional
    public CardBlockRequest approveBlockRequest(Long cardId, UUID processedBy) {
        log.debug("approveBlockRequest called with cardId={} by processedBy={}", cardId, processedBy);

        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCard_IdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();
        card.setStatus(cardStatusRepository.findByName(CardStatusType.BLOCKED)
                .orElseThrow(() -> new IllegalArgumentException("Card status not found")));

        cardRepository.save(card);

        blockRequest.setStatus(CardBlockRequest.Status.APPROVED);
        blockRequest.setProcessedAt(Instant.now());
        blockRequest.setProcessedBy(processedBy);
        CardBlockRequest saved = cardBlockRequestRepository.save(blockRequest);

        log.info("Approved block request for cardId={} by processedBy={}", cardId, processedBy);
        return saved;
    }

    /**
     * Rejects a pending card block request.
     *
     * @param cardId      The ID of the card for which the block request is to be rejected.
     * @param processedBy The ID of the user who processed the rejection.
     * @return The updated CardBlockRequest entity.
     * @throws IllegalArgumentException if a pending block request for the card is not found.
     * @throws IllegalArgumentException if the card status 'ACTIVE' is not found.
     */
    @Transactional
    public CardBlockRequest rejectBlockRequest(Long cardId, UUID processedBy) {
        log.debug("rejectBlockRequest called with cardId={} by processedBy={}", cardId, processedBy);

        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCard_IdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();
        card.setStatus(cardStatusRepository.findByName(CardStatusType.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Card status not found")));

        cardRepository.save(card);

        blockRequest.setStatus(CardBlockRequest.Status.REJECTED);
        blockRequest.setProcessedAt(Instant.now());
        blockRequest.setProcessedBy(processedBy);

        CardBlockRequest saved = cardBlockRequestRepository.save(blockRequest);

        log.info("Rejected block request for cardId={} by processedBy={}", cardId, processedBy);
        return saved;
    }
}