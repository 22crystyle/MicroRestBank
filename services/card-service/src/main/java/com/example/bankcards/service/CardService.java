package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
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
    public Page<Card> getCardsByOwner(UUID id, PageRequest pageRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
        return cardRepository.findAllByUser(user, pageRequest);
    }

    @Transactional(readOnly = true)
    public Card getById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException(id));
    }

    public boolean isOwner(Long cardId, UUID id) {
        boolean isOwner = cardRepository.existsByIdAndUser_Id(cardId, id);
        if (!isOwner) {
            throw new IsNotOwnerException("Card with id=" + cardId + " and owner with id=" + id + " not found.");
        }
        return cardRepository.existsByIdAndUser_Id(cardId, id);
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        return cardRepository.findAll(pageRequest);
    }

    @Transactional
    public void transfer(TransferRequest request, UUID id) {
        Card from = cardRepository.findById(request.fromCardId()).orElseThrow(
                () -> new CardNotFoundException(request.fromCardId()));
        Card to = cardRepository.findById(request.toCardId()).orElseThrow(
                () -> new CardNotFoundException(request.toCardId()));

        checkOwnership(from, id);
        checkOwnership(to, id);

        if (from.isBlocked()) throw new CardIsBlockedException(request.fromCardId());
        if (to.isBlocked()) throw new CardIsBlockedException(request.toCardId());

        from.withdraw(request.amount());
        to.deposit(request.amount());

        cardRepository.save(from);
        cardRepository.save(to);
    }

    private void checkOwnership(Card card, UUID userId) {
        if (!card.getUser().getId().equals(userId)) {
            throw new IsNotOwnerException("You are not owner of card " + card.getId());
        }
    }
}
