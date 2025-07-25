package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.util.CardStatusType;
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
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Integer DEFAULT_STATUS_ID = 1;

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardPanGeneratorFactory cardPanGeneratorFactory;

    @Transactional
    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public Card createCardForAccount(Long accountId) {
        Card card = new Card();
        String number;
        number = cardPanGeneratorFactory.getGenerator("mastercard").generateCardPan();
        card.setPan(number);
        card.setOwner(accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException(accountId)
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> new CardStatusNotFoundException(DEFAULT_STATUS_ID)
        ));
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.getCardsByOwnerId(userId);
    }

    @Transactional(readOnly = true)
    public Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException(id));
    }

    public boolean isOwner(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        return card.getOwner() != null && username != null && username.equals(card.getOwner().getUsername());
    }

    public boolean isOwner(String cardNumber, String username) {
        Card card = cardRepository.findByPan(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card with number " + cardNumber + " not found"));
        return card.getOwner() != null && username != null && username.equals(card.getOwner().getUsername());
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public void transfer(String fromCard, String toCard, BigDecimal amount, Principal principal) {
        Card first = cardRepository.findByPan(fromCard).orElseThrow(
                CardNotFoundException::new
        );
        Card second = cardRepository.findByPan(toCard).orElseThrow(
                CardNotFoundException::new
        );

        if (!first.getOwner().getUsername().equals(principal.getName()) ||
                !second.getOwner().getUsername().equals(principal.getName())) {
            throw new IsNotOwnerException("You are not owner of these cards");
        }

        if (first.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with PAN=" + first.getPan() + " is blocked");
        }

        if (second.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with PAN=" + second.getPan() + " is blocked");
        }

        if (amount.signum() <= 0) {
            throw new InvalidAmountException("You cannot transfer a negative value to a card");
        }

        if (first.getBalance().compareTo(amount) < 0) {
            throw new InvalidAmountException((first.getBalance().subtract(second.getBalance())));
        }

        first.setBalance(first.getBalance().subtract(amount));
        second.setBalance(second.getBalance().add(amount));
        cardRepository.save(first);
        cardRepository.save(second);
    }
}
