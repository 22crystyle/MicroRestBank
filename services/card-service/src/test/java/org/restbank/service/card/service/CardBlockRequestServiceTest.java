package org.restbank.service.card.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restbank.service.card.entity.*;
import org.restbank.service.card.exception.CardNotFoundException;
import org.restbank.service.card.exception.IsNotOwnerException;
import org.restbank.service.card.repository.CardBlockRequestRepository;
import org.restbank.service.card.repository.CardRepository;
import org.restbank.service.card.repository.CardStatusRepository;
import org.restbank.service.card.util.card.CardData;
import org.restbank.service.card.util.card.status.CardStatusData;
import org.restbank.service.card.util.user.UserData;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardBlockRequestServiceTest {
    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardStatusRepository cardStatusRepository;

    @InjectMocks
    private CardBlockRequestService service;

    private UUID ownerId;
    private Card card;
    private CardBlockRequest pendingRequest;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        User user = UserData.entity().withId(ownerId).build();

        card = CardData.entity().withId(1L).withOwner(user).build();
        pendingRequest = CardBlockRequest.builder()
                .id(10L)
                .card(card)
                .status(CardBlockRequest.Status.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void createBlockRequest_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.existsCardBlockRequestByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(false);

        service.createBlockRequest(1L, ownerId);

        verify(cardBlockRequestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void createBlockRequest_cardNotFound_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> service.createBlockRequest(1L, ownerId));
    }

    @Test
    void createBlockRequest_notOwner_throws() {
        UUID stranger = UUID.randomUUID();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(IsNotOwnerException.class,
                () -> service.createBlockRequest(1L, stranger));
    }

    @Test
    void createBlockRequest_alreadyPending_throws() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.existsCardBlockRequestByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createBlockRequest(1L, ownerId));
    }

    @Test
    void approveBlockRequest_success() {
        CardStatus blocked = CardStatusData.entity().withName(CardStatusType.BLOCKED).build();

        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.of(pendingRequest));
        when(cardStatusRepository.findByName(CardStatusType.BLOCKED)).thenReturn(Optional.of(blocked));
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        CardBlockRequest result = service.approveBlockRequest(1L, ownerId);

        assertAll("Approve block request",
                () -> assertEquals(CardBlockRequest.Status.APPROVED, result.getStatus()),
                () -> assertEquals(ownerId, result.getProcessedBy())
        );
        verify(cardRepository).save(card);
    }

    @Test
    void approveBlockRequest_noRequest_throws() {
        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.approveBlockRequest(1L, ownerId));
    }

    @Test
    void approveBlockRequest_noStatus_throws() {
        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.of(pendingRequest));
        when(cardStatusRepository.findByName(CardStatusType.BLOCKED)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.approveBlockRequest(1L, ownerId));
    }

    @Test
    void rejectBlockRequest_success() {
        CardStatus active = CardStatusData.entity().withName(CardStatusType.ACTIVE).build();

        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.of(pendingRequest));
        when(cardStatusRepository.findByName(CardStatusType.ACTIVE)).thenReturn(Optional.of(active));
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        CardBlockRequest result = service.rejectBlockRequest(1L, ownerId);

        assertAll("Reject block request",
                () -> assertEquals(CardBlockRequest.Status.REJECTED, result.getStatus()),
                () -> assertEquals(ownerId, result.getProcessedBy())
        );
        verify(cardRepository).save(card);
    }

    @Test
    void rejectBlockRequest_noRequest_throws() {
        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.rejectBlockRequest(1L, ownerId));
    }

    @Test
    void rejectBlockRequest_noStatus_throws() {
        when(cardBlockRequestRepository.findByCard_IdAndStatus(1L, CardBlockRequest.Status.PENDING))
                .thenReturn(Optional.of(pendingRequest));
        when(cardStatusRepository.findByName(CardStatusType.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.rejectBlockRequest(1L, ownerId));
    }
}
