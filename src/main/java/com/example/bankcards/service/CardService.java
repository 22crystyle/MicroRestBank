package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardIsBlockedException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Card createCardForAccount(Long accountId) {
        Card card = new Card();
        String number;
        do {
            number = generateMastercardNumber();
        } while (cardRepository.existsCardByCardNumber(number)); //TODO: оптимизировать постоянное обращение к `exists`
        card.setCardNumber(number);
        card.setOwner(accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account with id=" + accountId + " not found")
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> new IllegalArgumentException("Unknown status")
        ));
        return cardRepository.save(card);
    }

    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.getCardsByOwnerId(userId);
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card not found"));
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

    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public boolean transfer(String from, String to, BigDecimal amount) {
        Card first = cardRepository.findByCardNumber(from).orElseThrow(
                () -> new EntityNotFoundException("Card not found")
        );
        Card second = cardRepository.findByCardNumber(to).orElseThrow(
                () -> new EntityNotFoundException("Card not found")
        );

        if (amount.signum() < 0) {
            throw new DataIntegrityViolationException("You cannot transfer a negative value to a card");
        }

        if (first.getBalance().compareTo(amount) < 0) {
            throw new DataIntegrityViolationException("Not enough money: " + (first.getBalance().subtract(second.getBalance())) + " units");
        }

        if ("BLOCKED".equals(first.getStatus().getName()) || "BLOCKED".equals(second.getStatus().getName())) {
            throw new CardIsBlockedException();
        }

        first.setBalance(first.getBalance().subtract(amount));
        second.setBalance(second.getBalance().add(amount));
        cardRepository.save(first);
        cardRepository.save(second);
        return true;
    }
}
