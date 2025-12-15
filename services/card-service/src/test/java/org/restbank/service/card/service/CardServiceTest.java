package org.restbank.service.card.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restbank.libs.api.util.JwtPrincipal;
import org.restbank.service.card.dto.CardMapper;
import org.restbank.service.card.dto.request.TransferRequest;
import org.restbank.service.card.dto.response.CardResponse;
import org.restbank.service.card.entity.Card;
import org.restbank.service.card.entity.CardStatus;
import org.restbank.service.card.entity.User;
import org.restbank.service.card.exception.CardNotFoundException;
import org.restbank.service.card.exception.CardStatusNotFoundException;
import org.restbank.service.card.exception.IsNotOwnerException;
import org.restbank.service.card.exception.UserNotFoundException;
import org.restbank.service.card.repository.CardRepository;
import org.restbank.service.card.repository.CardStatusRepository;
import org.restbank.service.card.repository.UserRepository;
import org.restbank.service.card.util.card.CardData;
import org.restbank.service.card.util.card.status.CardStatusData;
import org.restbank.service.card.util.pan.CardPanGenerator;
import org.restbank.service.card.util.pan.CardPanGeneratorFactory;
import org.restbank.service.card.util.user.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
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
    @Mock
    private CardMapper cardMapper;
    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setup() {
        lenient().when(cardPanGeneratorFactory.getGenerator(anyString())).thenReturn(cardPanGenerator);
    }

    @Test
    void createCardForAccount_success() {
        User user = UserData.entity().withId(ownerId).build();
        CardStatus status = CardStatusData.entity().withId(1).build();
        String generatedPan = "5555666677778884";
        Card savedCard = CardData.entity().withId(10L).build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(cardPanGenerator.generateCardPan()).thenReturn(generatedPan);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        Card result = cardService.createCardForAccount(ownerId);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        Card capturedCard = captor.getValue();

        assertAll("Card properties",
                () -> assertEquals(generatedPan, capturedCard.getPan()),
                () -> assertEquals(user, capturedCard.getUser()),
                () -> assertEquals(YearMonth.now().plusYears(4), capturedCard.getExpiryDate()),
                () -> assertEquals(BigDecimal.ZERO, capturedCard.getBalance()),
                () -> assertEquals(status, capturedCard.getStatus())
        );

        assertEquals(savedCard, result);
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

        User owner = UserData.entity().withId(ownerId).build();
        Card from = spy(CardData.entity()
                .withId(fromId)
                .withOwner(owner)
                .build());
        Card to = spy(CardData.entity()
                .withId(toId)
                .withOwner(owner)
                .build());

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(from));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
    void transfer_toCardNotFound_throws() {
        long fromId = 1L, toId = 2L;
        Card from = CardData.entity().withId(fromId).build();
        when(cardRepository.findById(fromId)).thenReturn(Optional.of(from));
        when(cardRepository.findById(toId)).thenReturn(Optional.empty());

        TransferRequest req = mock(TransferRequest.class);
        when(req.fromCardId()).thenReturn(fromId);
        when(req.toCardId()).thenReturn(toId);

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
    void getCards_asAdmin_returnsMaskedResponse() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

        Page<Card> cardPage = new PageImpl<>(List.of(new Card()));
        when(cardRepository.findAll(pageRequest)).thenReturn(cardPage);

        cardService.getCards(pageRequest, auth);

        verify(cardMapper).toMaskedResponse(any(Card.class));
    }

    @Test
    void getCards_asUser_returnsFullResponse() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Authentication auth = mock(Authentication.class);

        try (MockedStatic<JwtPrincipal> mocked = mockStatic(JwtPrincipal.class)) {
            mocked.when(() -> JwtPrincipal.getId(auth)).thenReturn(ownerId.toString());
            doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))).when(auth).getAuthorities();

            User user = UserData.entity().withId(ownerId).build();
            when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

            Page<Card> cardPage = new PageImpl<>(List.of(new Card()));
            when(cardRepository.findAllByUser(user, pageRequest)).thenReturn(cardPage);

            cardService.getCards(pageRequest, auth);

            verify(cardMapper).toFullResponse(any(Card.class));
        }
    }

    @Test
    void getCard_asOwner_returnsFullResponse() {
        Authentication auth = mock(Authentication.class);
        try (MockedStatic<JwtPrincipal> mocked = mockStatic(JwtPrincipal.class)) {
            mocked.when(() -> JwtPrincipal.getId(auth)).thenReturn(ownerId.toString());

            User owner = UserData.entity().withId(ownerId).build();
            Card card = CardData.entity().withId(1L).withOwner(owner).build();
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

            cardService.getCard(1L, auth);

            verify(cardMapper).toFullResponse(card);
        }
    }

    @Test
    void getCard_asAdmin_notOwner_returnsMaskedResponse() {
        Authentication auth = mock(Authentication.class);
        UUID adminId = UUID.randomUUID();

        try (MockedStatic<JwtPrincipal> mocked = mockStatic(JwtPrincipal.class)) {
            mocked.when(() -> JwtPrincipal.getId(auth)).thenReturn(adminId.toString());
            doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

            User owner = UserData.entity().withId(ownerId).build();
            Card card = CardData.entity().withId(1L).withOwner(owner).build();
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

            cardService.getCard(1L, auth);

            verify(cardMapper).toMaskedResponse(card);
        }
    }

    @Test
    void getCard_asUser_notOwner_throwsException() {
        Authentication auth = mock(Authentication.class);
        UUID otherUserId = UUID.randomUUID();

        try (MockedStatic<JwtPrincipal> mocked = mockStatic(JwtPrincipal.class)) {
            mocked.when(() -> JwtPrincipal.getId(auth)).thenReturn(otherUserId.toString());
            doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))).when(auth).getAuthorities();

            User owner = UserData.entity().withId(ownerId).build();
            Card card = CardData.entity().withId(1L).withOwner(owner).build();
            when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

            assertThrows(IsNotOwnerException.class, () -> cardService.getCard(1L, auth));
        }
    }

    @Test
    void createCardForAccountAndGetMaskedResponse_success() {
        UUID userId = UUID.randomUUID();
        User user = UserData.entity().withId(userId).build();
        CardStatus status = CardStatusData.entity().withId(1).build();
        String generatedPan = "5555666677778884";
        Card savedCard = CardData.entity().withId(10L).build();
        CardResponse maskedResponse = mock(CardResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(cardPanGenerator.generateCardPan()).thenReturn(generatedPan);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toMaskedResponse(savedCard)).thenReturn(maskedResponse);

        CardResponse result = cardService.createCardForAccountAndGetMaskedResponse(userId);

        assertEquals(maskedResponse, result);
        verify(cardMapper).toMaskedResponse(savedCard);
    }
}
