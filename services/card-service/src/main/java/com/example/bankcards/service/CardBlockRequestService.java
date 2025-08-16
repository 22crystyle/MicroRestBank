package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.exception.CardNotFoundException;
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
    public void createBlockRequest(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        log.info("CreateBlockRequest found card: {}", card);

        if (cardBlockRequestRepository.existsCardBlockRequestByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)) {
            throw new IllegalArgumentException("Card block request already exists");
        }

        CardBlockRequest blockRequest = CardBlockRequest.builder()
                .card(card)
                .createdAt(Instant.now())
                .status(CardBlockRequest.Status.PENDING)
                .processedBy(null)
                .processedAt(null)
                .build();
        log.info("Created CardBlockRequest: {}", blockRequest);

        cardBlockRequestRepository.save(blockRequest);
    }

    @Transactional
    public CardBlockRequest approveBlockRequest(Long cardId, UUID processedBy) {
        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();

        card.setStatus(cardStatusRepository.findByName(("BLOCKED"))
                .orElseThrow(() -> new IllegalArgumentException("Card status not found")));

        cardRepository.save(card);
        blockRequest.setStatus(CardBlockRequest.Status.APPROVED);
        blockRequest.setProcessedAt(Instant.now());
        blockRequest.setProcessedBy(processedBy);
        return cardBlockRequestRepository.save(blockRequest);
    }

    @Transactional
    public CardBlockRequest rejectBlockRequest(Long cardId, UUID processedBy) {
        CardBlockRequest blockRequest = cardBlockRequestRepository.findByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Card block request not found"));

        Card card = blockRequest.getCard();

        card.setStatus(cardStatusRepository.findByName(("ACTIVE"))
                .orElseThrow(() -> new IllegalArgumentException("Card status not found")));

        cardRepository.save(card);
        blockRequest.setStatus(CardBlockRequest.Status.REJECTED);
        blockRequest.setProcessedAt(Instant.now());
        blockRequest.setProcessedBy(processedBy);
        return cardBlockRequestRepository.save(blockRequest);
    }
}