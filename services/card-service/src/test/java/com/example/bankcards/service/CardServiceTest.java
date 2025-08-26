package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardStatusNotFoundException;
import com.example.bankcards.exception.IsNotOwnerException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.card.CardData;
import com.example.bankcards.util.card.status.CardStatusData;
import com.example.bankcards.util.pan.CardPanGenerator;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import com.example.bankcards.util.user.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    private final UUID ownerId = UUID.randomUUID();
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardStatusRepository cardStatusRepository;
    @Mock
    private CardPanGeneratorFactory cardPanGeneratorFactory;
    @Mock
    private CardPanGenerator cardPanGenerator;
    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setup() {
        lenient().when(cardPanGeneratorFactory.getGenerator(anyString())).thenReturn(cardPanGenerator);
    }

    @Test
    void createCardForAccount_success() {
        User user = UserData.DEFAULT_ENTITY;
        user.setId(ownerId);

        CardStatus status = CardStatusData.DEFAULT_ENTITY;
        status.setId(1);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(cardPanGenerator.generateCardPan()).thenReturn("5555666677778884");

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        Card saved = CardData.DEFAULT_ENTITY;
        saved.setId(10L);
        when(cardRepository.save(any(Card.class))).thenReturn(saved);

        Card result = cardService.createCardForAccount(ownerId);

        verify(cardRepository).save(captor.capture());
        Card captured = captor.getValue();

        assertEquals("5555666677778884", captured.getPan());
        assertEquals(user, captured.getUser());
        assertEquals(YearMonth.now().plusYears(4), captured.getExpiryDate());
        assertEquals(BigDecimal.ZERO, captured.getBalance());
        assertEquals(status, captured.getStatus());

        assertEquals(saved, result);
    }

    @Test
    void createCardForAccount_userNotFound_throws() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        when(cardPanGenerator.generateCardPan()).thenReturn("0000");

        assertThrows(UserNotFoundException.class, () -> cardService.createCardForAccount(ownerId));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCardForAccount_statusNotFound_throws() {
        User user = UserData.DEFAULT_ENTITY;
        user.setId(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.empty());
        when(cardPanGenerator.generateCardPan()).thenReturn("0000");

        assertThrows(CardStatusNotFoundException.class, () -> cardService.createCardForAccount(ownerId));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getCardsByOwner_success() {
        User user = new User();
        user.setId(ownerId);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Card c1 = new Card();
        c1.setId(1L);
        Page<Card> page = new PageImpl<>(List.of(c1));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUser(user, pageRequest)).thenReturn(page);

        Page<Card> result = cardService.getCardsByOwner(ownerId, pageRequest);
        assertSame(page, result);
    }

    @Test
    void getCardsByOwner_userNotFound_throws() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> cardService.getCardsByOwner(ownerId, pageRequest));
    }

    @Test
    void getById_success() {
        Card card = new Card();
        card.setId(5L);
        when(cardRepository.findById(5L)).thenReturn(Optional.of(card));

        Card result = cardService.getById(5L);
        assertEquals(card, result);
    }

    @Test
    void getById_notFound_throws() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getById(99L));
    }

    @Test
    void getAllCards_returnsPage() {
        PageRequest pr = PageRequest.of(0, 5);
        Card card = new Card();
        card.setId(1L);
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(pr)).thenReturn(page);

        Page<Card> result = cardService.getAllCards(pr);
        assertSame(page, result);
    }

    @Test
    void transfer_success() {
        long fromId = 1L, toId = 2L;
        BigDecimal amount = new BigDecimal("100");

        Card from = spy(CardData.entity()
                .withId(fromId)
                .build());
        User owner = UserData.entity().withId(ownerId).build();
        from.setUser(owner);

        Card to = spy(CardData.entity()
                .withId(toId)
                .withOwner(owner)
                .build());

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(from));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(to));

        TransferRequest req = mock(TransferRequest.class);
        when(req.fromCardId()).thenReturn(fromId);
        when(req.toCardId()).thenReturn(toId);
        when(req.amount()).thenReturn(amount);

        cardService.transfer(req, ownerId);

        verify(from).withdraw(amount);
        verify(to).deposit(amount);

        verify(cardRepository).save(from);
        verify(cardRepository).save(to);
    }

    @Test
    void transfer_fromCardNotFound_throws() {
        long fromId = 1L;
        when(cardRepository.findById(fromId)).thenReturn(Optional.empty());
        TransferRequest req = mock(TransferRequest.class);
        when(req.fromCardId()).thenReturn(fromId);

        assertThrows(CardNotFoundException.class, () -> cardService.transfer(req, ownerId));
    }

    @Test
    void transfer_notOwner_throws() {
        long fromId = 1L, toId = 2L;

        Card from = CardData.entity().withId(fromId).build();
        User other = UserData.entity().withId(UUID.randomUUID()).build();
        from.setUser(other);

        Card to = CardData.entity().withId(toId).withOwner(other).build();

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(from));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(to));

        TransferRequest req = mock(TransferRequest.class);
        when(req.fromCardId()).thenReturn(fromId);
        when(req.toCardId()).thenReturn(toId);

        assertThrows(IsNotOwnerException.class, () -> cardService.transfer(req, ownerId));
    }

    @Test
    void checkOwnership_public_true() {
        when(cardRepository.existsByIdAndUser_Id(1L, ownerId)).thenReturn(true);
        boolean result = cardService.checkOwnership(1L, ownerId);
        assertTrue(result);
        verify(cardRepository, times(1)).existsByIdAndUser_Id(1L, ownerId);
    }

    @Test
    void checkOwnership_public_false_throws() {
        when(cardRepository.existsByIdAndUser_Id(1L, ownerId)).thenReturn(false);
        assertThrows(IsNotOwnerException.class, () -> cardService.checkOwnership(1L, ownerId));
        verify(cardRepository, times(1)).existsByIdAndUser_Id(1L, ownerId);
    }
}
