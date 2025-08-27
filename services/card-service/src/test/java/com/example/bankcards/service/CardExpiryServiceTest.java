package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardStatusType;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.util.card.CardData;
import com.example.bankcards.util.card.status.CardStatusData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardExpiryServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardStatusRepository cardStatusRepository;

    @InjectMocks
    private CardExpiryService cardExpiryService;

    @Test
    void markExpiredCards_shouldUpdateExpiredCards() {
        CardStatus expiredStatus = CardStatusData.entity().withName(CardStatusType.EXPIRED).build();

        Card card = CardData.entity().withExpiryDate(YearMonth.now()).build();

        when(cardStatusRepository.findByName(CardStatusType.EXPIRED.name())).thenReturn(Optional.of(expiredStatus));
        when(cardRepository.findByExpiryDate(YearMonth.now())).thenReturn(List.of(card));

        cardExpiryService.markExpiredCards();

        assertEquals(CardStatusType.EXPIRED, card.getStatus().getName());
        verify(cardRepository).saveAll(List.of(card));
    }

    @Test
    void markExpiredCards_shouldThrowException_ifExpiredStatusNotFound() {
        when(cardStatusRepository.findByName(CardStatusType.EXPIRED.name())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> cardExpiryService.markExpiredCards());
    }

    @Test
    void markExpiredCards_shouldNotSave_ifNoExpiredCards() {
        CardStatus expiredStatus = CardStatusData.entity().withName(CardStatusType.EXPIRED).build();

        when(cardStatusRepository.findByName(CardStatusType.EXPIRED.name())).thenReturn(Optional.of(expiredStatus));
        when(cardRepository.findByExpiryDate(YearMonth.now())).thenReturn(List.of());

        cardExpiryService.markExpiredCards();

        verify(cardRepository, never()).saveAll(anyList());
    }
}
