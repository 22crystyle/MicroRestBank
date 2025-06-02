package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardIsBlockedException;
import com.example.bankcards.exception.InvalidAmountException;
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
        } while (cardRepository.existsCardByCardNumber(number)); // TODO: ловить uq_constraint для cardNumber, и создавать новый pan
        card.setCardNumber(number);
        card.setOwner(accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account with id=" + accountId + " not found") // TODO: возвращать и обрабатывать собственные ошибки
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> new IllegalArgumentException("Unknown status")
        ));
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.getCardsByOwnerId(userId);
    }

    @Transactional(readOnly = true)
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

//  TODO: возвращать Page<CardResponse> через JPQL с использованием @Query, чтобы отказаться отказаться от лишнего маппинга на уровне сервиса и контроллера
//   @Query("select new com.example.dto.CardResponse(c.id, c.maskedNumber, c.balance, ...) "
//   + "from Card c where c.owner.id = :userId")
    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public boolean transfer(String fromCard, String toCard, BigDecimal amount) {
        Card first = cardRepository.findByCardNumber(fromCard).orElseThrow(
                () -> new EntityNotFoundException("Card not found")
        );
        Card second = cardRepository.findByCardNumber(toCard).orElseThrow(
                () -> new EntityNotFoundException("Card not found")
        );

        if (amount.signum() <= 0) {
            throw new InvalidAmountException("You cannot transfer a negative value to a card");
        }

        // TODO: возвращать и обрабатывать собственные ошибки
        if (first.getBalance().compareTo(amount) < 0) {
            throw new DataIntegrityViolationException("Not enough money: " + (first.getBalance().subtract(second.getBalance())) + " units");
        }

        // TODO: проверка через CardStatusType вместо строкового представления
        if ("BLOCKED".equals(first.getStatus().getName()) || "BLOCKED".equals(second.getStatus().getName())) {
            throw new CardIsBlockedException("Card with number=" + first.getCardNumber() + "or with number=" + second.getCardNumber() + " is blocked");
        }

        first.setBalance(first.getBalance().subtract(amount));
        second.setBalance(second.getBalance().add(amount));
        cardRepository.save(first);
        cardRepository.save(second);
        return true;
    }
}
