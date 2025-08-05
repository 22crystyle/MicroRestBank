package com.example.bankcards.service;

import com.example.bankcards.exception.*;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import com.example.shared.dto.request.TransferRequest;
import com.example.shared.entity.Card;
import com.example.shared.entity.CardStatusType;
import com.example.shared.entity.User;
import com.example.shared.repository.CardRepository;
import com.example.shared.repository.CardStatusRepository;
import com.example.shared.repository.UserRepository;
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
    public Card createCardForAccount(Long accountId) {
        Card card = new Card();
        String number;
        number = cardPanGeneratorFactory.getGenerator("mastercard").generateCardPan();
        card.setPan(number);
        card.setOwner(userRepository.findById(accountId).orElseThrow(
                () -> new UserNotFoundException(accountId)
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> new CardStatusNotFoundException(DEFAULT_STATUS_ID)
        ));
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<Card> getByOwner(Long userId) {
        return cardRepository.getCardsByOwnerId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Card> getByOwner(String username, PageRequest pageRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return cardRepository.findAllByOwner(user, pageRequest);
    }

    @Transactional(readOnly = true)
    public Card getById(Long id) {
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
    public void transfer(TransferRequest request, String username) {
        Card from = cardRepository.findById(request.fromCardId()).orElseThrow(
                () -> new CardNotFoundException(request.fromCardId())
        );
        Card to = cardRepository.findById(request.toCardId()).orElseThrow(
                () -> new CardNotFoundException(request.toCardId())
        );

        if (!from.getOwner().getUsername().equals(username) ||
                !to.getOwner().getUsername().equals(username)) {
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
