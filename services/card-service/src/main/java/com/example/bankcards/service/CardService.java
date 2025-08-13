package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatusType;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Integer DEFAULT_STATUS_ID = 1;

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardPanGeneratorFactory cardPanGeneratorFactory;

    @Transactional
    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public Card createCardForAccount(UUID userId) {
        Card card = new Card();
        String number;
        number = cardPanGeneratorFactory.getGenerator("mastercard").generateCardPan();
        card.setPan(number);
        card.setUser(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.valueOf(userId))
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> new CardStatusNotFoundException(DEFAULT_STATUS_ID)
        ));
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<Card> getByOwner(UUID userId) {
        return cardRepository.getCardsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Card> getByOwner(String username, PageRequest pageRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return cardRepository.findAllByUser(user, pageRequest);
    }

    @Transactional(readOnly = true)
    public Card getById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException(id));
    }

    public boolean isOwner(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        return card.getUser() != null && username != null && username.equals(card.getUser().getUsername());
    }

    public boolean isOwner(String cardNumber, String username) {
        Card card = cardRepository.findByPan(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card with number " + cardNumber + " not found"));
        return card.getUser() != null && username != null && username.equals(card.getUser().getUsername());
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public void transfer(TransferRequest request, String username) {
        Card from = cardRepository.findById(request.fromCardId()).orElseThrow(
                () -> new CardNotFoundException(request.fromCardId())
        );
        Card to = cardRepository.findById(request.toCardId()).orElseThrow(
                () -> new CardNotFoundException(request.toCardId())
        );

        if (!from.getUser().getUsername().equals(username) ||
                !to.getUser().getUsername().equals(username)) {
            throw new IsNotOwnerException("You are not owner of these cards");
        }

        if (from.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with id=" + request.fromCardId() + " is blocked");
        }

        if (to.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with id=" + request.toCardId() + " is blocked");
        }

        if (request.amount().signum() <= 0) {
            throw new InvalidAmountException("You cannot transfer a negative value to a card");
        }

        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new InvalidAmountException((from.getBalance().subtract(to.getBalance())));
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));
        cardRepository.save(from);
        cardRepository.save(to);
    }
}
