package com.example.bankcards.service;

import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.util.data.account.AccountData;
import com.example.bankcards.util.data.card.CardData;
import com.example.bankcards.util.data.card.status.CardStatusData;
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
import java.security.Principal;
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
    private AccountRepository accountRepository;

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
        Account account = AccountData.DEFAULT_ENTITY;
        CardStatus cardStatus = CardStatusData.DEFAULT_ENTITY;
        Card card = CardData.DEFAULT_ENTITY;
        String cardNumber = "1234567890123456";

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.of(cardStatus));
        when(cardPanGeneratorFactory.getGenerator("mastercard")).thenReturn(cardPanGenerator);
        when(cardPanGenerator.generateCardPan()).thenReturn(cardNumber);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card result = service.createCardForAccount(accountId);

        assertEquals(card, result);
        verify(accountRepository).findById(accountId);
        verify(cardStatusRepository).findById(1);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCardForAccount_whenAccountNotExists_thenThrowException() {
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(cardPanGeneratorFactory.getGenerator("mastercard")).thenReturn(cardPanGenerator);

        assertThrows(AccountNotFoundException.class, () -> service.createCardForAccount(accountId));
        verify(accountRepository).findById(accountId);
        verifyNoInteractions(cardStatusRepository, cardRepository);
    }

    @Test
    void getCardsByUserId_whenCalled_thenReturnCardList() {
        Long userId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        List<Card> cards = List.of(card);
        when(cardRepository.getCardsByOwnerId(userId)).thenReturn(cards);

        List<Card> result = service.getCardsByUserId(userId);

        assertEquals(cards, result);
        verify(cardRepository).getCardsByOwnerId(userId);
    }

    @Test
    void getCard_whenCardExists_thenReturnCard() {
        Long cardId = 1L;
        Card card = CardData.DEFAULT_ENTITY;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Card result = service.getCard(cardId);

        assertEquals(card, result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCard_whenCardNotExists_thenThrowException() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.getCard(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardId_whenOwnerMatches_thenReturnTrue() {
        Long cardId = 1L;
        String username = "user";
        Card card = CardData.entity().withOwner(AccountData.entity().withUsername(username).build()).build();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = service.isOwner(cardId, username);

        assertTrue(result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardId_whenOwnerDoesNotMatch_thenReturnFalse() {
        Long cardId = 1L;
        String username = "user";
        Card card = CardData.entity().withOwner(AccountData.entity().withUsername("other").build()).build();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = service.isOwner(cardId, username);

        assertFalse(result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void isOwner_byCardNumber_whenOwnerMatches_thenReturnTrue() {
        String cardNumber = "1234567890123456";
        String username = "user";
        Card card = CardData.entity().withOwner(AccountData.entity().withUsername(username).build()).build();
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
        String fromCard = "1234";
        String toCard = "5678";
        BigDecimal amount = new BigDecimal("100.00");
        Principal principal = () -> "user";
        Card from = CardData.entity()
                .withPan(fromCard)
                .withBalance(new BigDecimal("200.00"))
                .withOwner(AccountData.entity().withUsername("user").build())
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();
        Card to = CardData.entity()
                .withPan(toCard)
                .withBalance(BigDecimal.ZERO)
                .withOwner(AccountData.entity().withUsername("user").build())
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();

        when(cardRepository.findByPan(fromCard)).thenReturn(Optional.of(from));
        when(cardRepository.findByPan(toCard)).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenReturn(from).thenReturn(to);

        service.transfer(fromCard, toCard, amount, principal);

        verify(cardRepository).save(from);
        verify(cardRepository).save(to);
        assertEquals(new BigDecimal("100.00"), from.getBalance());
        assertEquals(new BigDecimal("100.00"), to.getBalance());
    }

    @Test
    void transfer_whenNotOwner_thenThrowException() {
        String fromCard = "1234";
        String toCard = "5678";
        BigDecimal amount = new BigDecimal("100.00");
        Principal principal = () -> "other";
        Card from = CardData.entity()
                .withPan(fromCard)
                .withOwner(AccountData.entity().withUsername("user").build())
                .build();
        Card to = CardData.entity()
                .withPan(toCard)
                .withOwner(AccountData.entity().withUsername("user").build())
                .build();

        when(cardRepository.findByPan(fromCard)).thenReturn(Optional.of(from));
        when(cardRepository.findByPan(toCard)).thenReturn(Optional.of(to));

        assertThrows(IsNotOwnerException.class, () -> service.transfer(fromCard, toCard, amount, principal));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void transfer_whenCardBlocked_thenThrowException() {
        String fromCard = "1234";
        String toCard = "5678";
        BigDecimal amount = new BigDecimal("100.00");
        Principal principal = () -> "user";
        Card from = CardData.entity()
                .withPan(fromCard)
                .withOwner(AccountData.entity().withUsername("user").build())
                .withCardStatus(CardStatusData.entity().withName("BLOCKED").build())
                .build();
        Card to = CardData.entity()
                .withPan(toCard)
                .withOwner(AccountData.entity().withUsername("user").build())
                .build();

        when(cardRepository.findByPan(fromCard)).thenReturn(Optional.of(from));
        when(cardRepository.findByPan(toCard)).thenReturn(Optional.of(to));

        assertThrows(CardIsBlockedException.class, () -> service.transfer(fromCard, toCard, amount, principal));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void transfer_whenInsufficientFunds_thenThrowException() {
        String fromCard = "1234";
        String toCard = "5678";
        BigDecimal amount = new BigDecimal("100.00");
        Principal principal = () -> "user";
        Card from = CardData.entity()
                .withPan(fromCard)
                .withBalance(new BigDecimal("50.00"))
                .withOwner(AccountData.entity().withUsername("user").build())
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();
        Card to = CardData.entity()
                .withPan(toCard)
                .withOwner(AccountData.entity().withUsername("user").build())
                .withCardStatus(CardStatusData.entity().withName("ACTIVE").build())
                .build();

        when(cardRepository.findByPan(fromCard)).thenReturn(Optional.of(from));
        when(cardRepository.findByPan(toCard)).thenReturn(Optional.of(to));

        assertThrows(InvalidAmountException.class, () -> service.transfer(fromCard, toCard, amount, principal));
        verifyNoMoreInteractions(cardRepository);
    }
}
