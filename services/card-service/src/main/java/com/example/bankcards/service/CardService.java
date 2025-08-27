package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardStatusNotFoundException;
import com.example.bankcards.exception.IsNotOwnerException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.MaskingUtils;
import com.example.bankcards.util.pan.CardPanGeneratorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.debug("createCardForAccount called for userId={}", userId);

        Card card = new Card();
        String number;
        number = cardPanGeneratorFactory.getGenerator("mastercard").generateCardPan();
        card.setPan(number);
        card.setUser(userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id {} not found when creating card", userId);
                    return new UserNotFoundException(userId);
                }
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(DEFAULT_STATUS_ID).orElseThrow(
                () -> {
                    log.error("Default card status with id {} not found", DEFAULT_STATUS_ID);
                    return new CardStatusNotFoundException(DEFAULT_STATUS_ID);
                }
        ));
        Card saved = cardRepository.save(card);

        String panLast4 = MaskingUtils.maskCardNumber(number);
        log.info("Created card id={} for userId={} panLast4={} expiry={}",
                saved.getId(), userId, panLast4, saved.getExpiryDate());

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsByOwner(UUID id, PageRequest pageRequest) {
        log.debug("getCardsByOwner called for userId={} page={} size={}", id, pageRequest.getPageNumber(), pageRequest.getPageSize());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        Page<Card> page = cardRepository.findAllByUser(user, pageRequest);
        log.info("Returning {} cards for userId={} (page {}, size {})", page.getNumberOfElements(), id, page.getNumber(), page.getSize());
        return page;
    }

    @Transactional(readOnly = true)
    public Card getById(Long id) {
        log.debug("getById called for cardId={}", id);
        return cardRepository.findById(id).orElseThrow(() -> {
            log.warn("Card {} not found", id);
            return new CardNotFoundException(id);
        });
    }

    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        log.debug("getAllCards called page={} size={}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<Card> page = cardRepository.findAll(pageRequest);
        log.info("Returning {} cards (page {}, size {})", page.getNumberOfElements(), page.getNumber(), page.getSize());
        return page;
    }

    @Transactional
    public void transfer(TransferRequest request, UUID userId) {
        log.debug("transfer called fromCardId={} toCardId={} amount={} by userId={}",
                request.fromCardId(), request.toCardId(), request.amount(), userId);

        Card from = cardRepository.findById(request.fromCardId()).orElseThrow(
                () -> {
                    log.warn("Source card {} not found for transfer by user {}", request.fromCardId(), userId);
                    return new CardNotFoundException(request.fromCardId());
                });
        Card to = cardRepository.findById(request.toCardId()).orElseThrow(
                () -> {
                    log.warn("Destination card {} not found for transfer by user {}", request.toCardId(), userId);
                    return new CardNotFoundException(request.toCardId());
                });

        checkOwnership(from, userId);
        checkOwnership(to, userId);

        from.withdraw(request.amount());
        to.deposit(request.amount());

        Card savedFrom = cardRepository.save(from);
        Card savedTo = cardRepository.save(to);

        log.info("Transfer completed: {} {} -> {} {} by userId={}. Balances after transfer: fromId={} balance={}, toId={} balance={}",
                request.amount(), savedFrom.getId(), request.amount(), savedTo.getId(), userId,
                savedFrom.getId(), savedFrom.getBalance(), savedTo.getId(), savedTo.getBalance());
    }

    public boolean checkOwnership(Long cardId, UUID id) {
        boolean isOwner = cardRepository.existsByIdAndUser_Id(cardId, id);
        if (!isOwner) {
            log.warn("Ownership check failed for cardId={} userId={}", cardId, id);
            throw new IsNotOwnerException("Card with id=" + cardId + " and owner with id=" + id + " not found.");
        }
        log.debug("Ownership check passed for cardId={} userId={}", cardId, id);
        return true;
    }

    private void checkOwnership(Card card, UUID userId) {
        if (!card.getUser().getId().equals(userId)) {
            log.warn("User {} is not owner of card {}", userId, card.getId());
            throw new IsNotOwnerException("You are not owner of card " + card.getId());
        }
        log.debug("Ownership validated for user {} and card {}", userId, card.getId());
    }
}
