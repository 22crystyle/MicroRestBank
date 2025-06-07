package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.util.CardStatusType;
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

import static com.example.bankcards.util.MastercardGenerator.generateMastercardNumber;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Integer DEFAULT_STATUS_ID = 1;

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardStatusRepository cardStatusRepository;

    @Transactional
    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public Card createCardForAccount(Long accountId) {
        Card card = new Card();
        String number;
        number = generateMastercardNumber();
        card.setCardNumber(number);
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

    public boolean isOwner(Long cardId, Principal principal) {
        if (principal == null) {
            return false;
        }

        return cardRepository.existsCardByIdAndOwner_Username(cardId, principal.getName());
    }

    public boolean isOwner(String cardNumber, Principal principal) {
        if (principal == null) {
            return false;
        }

        return cardRepository.existsCardByCardNumberAndOwner_Username(cardNumber, principal.getName());
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public boolean transfer(String fromCard, String toCard, BigDecimal amount) {
        Card first = cardRepository.findByCardNumber(fromCard).orElseThrow(
                CardNotFoundException::new
        );
        Card second = cardRepository.findByCardNumber(toCard).orElseThrow(
                CardNotFoundException::new
        );

        if (amount.signum() <= 0) {
            throw new InvalidAmountException("You cannot transfer a negative value to a card");
        }

        if (first.getBalance().compareTo(amount) < 0) {
            throw new InvalidAmountException((first.getBalance().subtract(second.getBalance())));
        }

        if (first.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with PAN=" + first.getCardNumber() + " is blocked");
        } else if (second.getStatus().getCardStatusType() == CardStatusType.BLOCKED) {
            throw new CardIsBlockedException("Card with PAN=" + second.getCardNumber() + " is blocked");
        }

        first.setBalance(first.getBalance().subtract(amount));
        second.setBalance(second.getBalance().add(amount));
        cardRepository.save(first);
        cardRepository.save(second);
        return true;
    }
}
