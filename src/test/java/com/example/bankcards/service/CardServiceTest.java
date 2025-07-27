package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.data.card.CardData;
import com.example.bankcards.util.data.card.status.CardStatusData;
import com.example.bankcards.util.data.user.UserData;
import com.example.bankcards.util.pan.CardPanGenerator;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
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
    private CardService service;

    @Test
    void createCardForAccount_whenAccountExists_thenReturnCard() {
        Long accountId = 1L;
        User user = UserData.DEFAULT_ENTITY;
        CardStatus cardStatus = CardStatusData.DEFAULT_ENTITY;
        Card card = CardData.DEFAULT_ENTITY;
        String cardNumber = "1234567890123456";

        when(userRepository.findById(accountId)).thenReturn(Optional.of(user));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.of(cardStatus));
        when(cardPanGeneratorFactory.getGenerator("mastercard")).thenReturn(cardPanGenerator);
        when(cardPanGenerator.generateCardPan()).thenReturn(cardNumber);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card result = service.createCardForAccount(accountId);

        assertEquals(card, result);
        verify(userRepository).findById(accountId);
        verify(cardStatusRepository).findById(1);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCardForAccount_whenAccountNotExists_thenThrowException() {
        Long accountId = 1L;
        when(userRepository.findById(accountId)).thenReturn(Optional.empty());
        when(cardPanGeneratorFactory.getGenerator("mastercard")).thenReturn(cardPanGenerator);

        assertThrows(UserNotFoundException.class, () -> service.createCardForAccount(accountId));
        verify(userRepository).findById(accountId);
        verifyNoInteractions(cardStatusRepository, cardRepository);
    }

    @Test
    void getByOwner_whenCalled_thenReturnCardList() {
        Long userId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        List<Card> cards = List.of(card);
        when(cardRepository.getCardsByOwnerId(userId)).thenReturn(cards);

        List<Card> result = service.getByOwner(userId);

        assertEquals(cards, result);
        verify(cardRepository).getCardsByOwnerId(userId);
    }

    @Test
    void getCard_whenCardExists_thenReturnById() {
        Long cardId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Card result = service.getById(cardId);

        assertEquals(card, result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCard_whenByIdNotExists_thenThrowException() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.getById(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardId_whenOwnerMatches_thenReturnTrue() {
        Long cardId = 1L;
        String username = "user";
        Card card = CardData.entity().withOwner(UserData.entity().withUsername(username).build()).build();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = service.isOwner(cardId, username);

        assertTrue(result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardId_whenOwnerDoesNotMatch_thenReturnFalse() {
        Long cardId = 1L;
        String username = "user";
        Card card = CardData.entity().withOwner(UserData.entity().withUsername("other").build()).build();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = service.isOwner(cardId, username);

        assertFalse(result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardNumber_whenOwnerMatches_thenReturnTrue() {
        String cardNumber = "1234567890123456";
        String username = "user";
        Card card = CardData.entity().withOwner(UserData.entity().withUsername(username).build()).build();
        when(cardRepository.findByPan(cardNumber)).thenReturn(Optional.of(card));

        boolean result = service.isOwner(cardNumber, username);

        assertTrue(result);
        verify(cardRepository).findByPan(cardNumber);
    }

    @Test
    void getAllCards_whenCalled_thenReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Card card = CardData.DEFAULT_ENTITY;
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(pageRequest)).thenReturn(page);

        Page<Card> result = service.getAllCards(pageRequest);

        assertEquals(page, result);
        verify(cardRepository).findAll(pageRequest);
    }

    @Test
    void transfer_whenValid_thenSuccess() {
        BigDecimal amount = new BigDecimal("100.00");
        Card from = CardData.entity()
                .withId(1L)
                .withBalance(amount)
                .withOwner(UserData.DEFAULT_ENTITY)
                .withCardStatus(CardStatusData.DEFAULT_ENTITY)
                .build();
        Card to = CardData.entity()
                .withId(2L)
                .withBalance(BigDecimal.ZERO)
                .withOwner(UserData.DEFAULT_ENTITY)
                .withCardStatus(CardStatusData.DEFAULT_ENTITY)
                .build();
        TransferRequest request = new TransferRequest(from.getId(), to.getId(), amount);

        when(cardRepository.findById(from.getId())).thenReturn(Optional.of(from));
        when(cardRepository.findById(to.getId())).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenReturn(from).thenReturn(to);

        service.transfer(request, "user");

        verify(cardRepository).save(from);
        verify(cardRepository).save(to);
        assertEquals(new BigDecimal("0.00"), from.getBalance());
        assertEquals(new BigDecimal("100.00"), to.getBalance());
    }

    @Test
    void transfer_whenNotOwner_thenThrowException() {
        BigDecimal amount = new BigDecimal("100.00");
        Card from = CardData.entity()
                .withId(1L)
                .withBalance(amount)
                .withOwner(UserData.DEFAULT_ENTITY)
                .build();
        Card to = CardData.entity()
                .withId(2L)
                .withOwner(UserData.DEFAULT_ENTITY)
                .build();
        TransferRequest request = new TransferRequest(from.getId(), to.getId(), amount);

        when(cardRepository.findById(from.getId())).thenReturn(Optional.of(from));
        when(cardRepository.findById(to.getId())).thenReturn(Optional.of(to));

        assertThrows(IsNotOwnerException.class, () -> service.transfer(request, "other"));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void transfer_whenCardBlocked_thenThrowException() {
        BigDecimal amount = new BigDecimal("100.00");
        Card from = CardData.entity()
                .withId(1L)
                .withBalance(amount)
                .withOwner(UserData.DEFAULT_ENTITY)
                .withCardStatus(CardStatusData.entity().withName("BLOCKED").build())
                .build();
        Card to = CardData.entity()
                .withId(2L)
                .withOwner(UserData.DEFAULT_ENTITY)
                .build();

        when(cardRepository.findById(from.getId())).thenReturn(Optional.of(from));
        when(cardRepository.findById(to.getId())).thenReturn(Optional.of(to));
        TransferRequest request = new TransferRequest(from.getId(), to.getId(), amount);

        assertThrows(CardIsBlockedException.class, () -> service.transfer(request, "user"));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void transfer_whenInsufficientFunds_thenThrowException() {
        BigDecimal amount = new BigDecimal("100.00");
        Card from = CardData.entity()
                .withBalance(new BigDecimal("50.00"))
                .withOwner(UserData.DEFAULT_ENTITY)
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();
        Card to = CardData.entity()
                .withOwner(UserData.DEFAULT_ENTITY)
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();
        TransferRequest request = new TransferRequest(from.getId(), to.getId(), amount);

        when(cardRepository.findById(from.getId())).thenReturn(Optional.of(from));
        when(cardRepository.findById(to.getId())).thenReturn(Optional.of(to));

        assertThrows(InvalidAmountException.class, () -> service.transfer(request, "user"));
        verifyNoMoreInteractions(cardRepository);
    }
}
