package com.example.bankcards.service;

import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.util.data.card.CardData;
import com.example.bankcards.util.data.card.status.CardStatusData;
import com.example.entity.Card;
import com.example.entity.CardBlockRequest;
import com.example.entity.CardStatus;
import com.example.repository.CardBlockRequestRepository;
import com.example.repository.CardRepository;
import com.example.repository.CardStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardBlockRequestServiceTest {
    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardStatusRepository cardStatusRepository;

    @InjectMocks
    private CardBlockRequestService service;

    @Test
    void createBlockRequest_whenValid_thenSuccess() {
        Long cardId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.existsCardBlockRequestByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)).thenReturn(false);
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class))).thenReturn(new CardBlockRequest());

        service.createBlockRequest(cardId);

        verify(cardRepository).findById(cardId);
        verify(cardBlockRequestRepository).existsCardBlockRequestByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING);
        verify(cardBlockRequestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void createBlockRequest_whenCardNotFound_thenThrowException() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.createBlockRequest(cardId));
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(cardBlockRequestRepository);
    }

    @Test
    void createBlockRequest_whenPendingRequestExists_thenThrowException() {
        Long cardId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.existsCardBlockRequestByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createBlockRequest(cardId));
        verify(cardRepository).findById(cardId);
        verify(cardBlockRequestRepository).existsCardBlockRequestByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING);
        verifyNoMoreInteractions(cardBlockRequestRepository);
    }

    @Test
    void approveBlockRequest_whenValid_thenSuccess() {
        Long cardId = 1L;
        Long processedBy = 2L;
        Card card = CardData.DEFAULT_ENTITY;
        CardStatus blockedStatus = CardStatusData.entity().withName("BLOCKED").build();
        CardBlockRequest blockRequest = CardBlockRequest.builder()
                .card(card)
                .status(CardBlockRequest.Status.PENDING)
                .build();

        when(cardBlockRequestRepository.findByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)).thenReturn(Optional.of(blockRequest));
        when(cardStatusRepository.findByName("BLOCKED")).thenReturn(Optional.of(blockedStatus));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardBlockRequestRepository.save(blockRequest)).thenReturn(blockRequest);

        CardBlockRequest result = service.approveBlockRequest(cardId, processedBy);

        assertEquals(CardBlockRequest.Status.APPROVED, result.getStatus());
        assertEquals(processedBy, result.getProcessedBy());
        assertNotNull(result.getProcessedAt());
        verify(cardRepository).save(card);
        verify(cardBlockRequestRepository).save(blockRequest);
    }

    @Test
    void rejectBlockRequest_whenValid_thenSuccess() {
        Long cardId = 1L;
        Long processedBy = 2L;
        Card card = CardData.DEFAULT_ENTITY;
        CardStatus activeStatus = CardStatusData.entity().withName("ACTIVE").build();
        CardBlockRequest blockRequest = CardBlockRequest.builder()
                .card(card)
                .status(CardBlockRequest.Status.PENDING)
                .build();

        when(cardBlockRequestRepository.findByCardIdAndStatus(cardId, CardBlockRequest.Status.PENDING)).thenReturn(Optional.of(blockRequest));
        when(cardStatusRepository.findByName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardBlockRequestRepository.save(blockRequest)).thenReturn(blockRequest);

        CardBlockRequest result = service.rejectBlockRequest(cardId, processedBy);

        assertEquals(CardBlockRequest.Status.REJECTED, result.getStatus());
        assertEquals(processedBy, result.getProcessedBy());
        assertNotNull(result.getProcessedAt());
        verify(cardRepository).save(card);
        verify(cardBlockRequestRepository).save(blockRequest);
    }
}


























