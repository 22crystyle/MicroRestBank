package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.CardStatusType;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.IsNotOwnerException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardBlockRequestService {

    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;

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

    @Transactional
    public CardBlockRequest approveBlockRequest(Long cardId, UUID processedBy) {
        log.debug("approveBlockRequest called with cardId={} by processedBy={}", cardId, processedBy);

        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCard_IdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();
        card.setStatus(cardStatusRepository.findByName(CardStatusType.BLOCKED.name())
                .orElseThrow(() -> new IllegalArgumentException("Card status not found")));

        cardRepository.save(card);

        blockRequest.setStatus(CardBlockRequest.Status.APPROVED);
        blockRequest.setProcessedAt(Instant.now());
        blockRequest.setProcessedBy(processedBy);
        CardBlockRequest saved = cardBlockRequestRepository.save(blockRequest);

        log.info("Approved block request for cardId={} by processedBy={}", cardId, processedBy);
        return saved;
    }

    @Transactional
    public CardBlockRequest rejectBlockRequest(Long cardId, UUID processedBy) {
        log.debug("rejectBlockRequest called with cardId={} by processedBy={}", cardId, processedBy);

        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCard_IdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();
        card.setStatus(cardStatusRepository.findByName(CardStatusType.ACTIVE.name())
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