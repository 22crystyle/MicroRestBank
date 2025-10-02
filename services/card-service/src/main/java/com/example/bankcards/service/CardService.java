package com.example.bankcards.service;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.CardResponse;
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
import com.example.shared.util.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Service for managing bank cards. Provides functionality for creating, retrieving, and transferring funds between cards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private static final Integer DEFAULT_STATUS_ID = 1;

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardPanGeneratorFactory cardPanGeneratorFactory;
    private final CardMapper cardMapper;

    /**
     * Creates a new card for a given user account.
     *
     * @param userId The ID of the user for whom the card is to be created.
     * @return The newly created Card entity.
     * @throws UserNotFoundException if the user with the given ID is not found.
     * @throws CardStatusNotFoundException if the default card status is not found.
     */
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

    /**
     * Retrieves a paginated list of cards owned by a specific user.
     *
     * @param id The ID of the user (owner) whose cards are to be retrieved.
     * @param pageRequest The pagination information.
     * @return A Page of Card entities.
     * @throws UserNotFoundException if the user with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public Page<Card> getCardsByOwner(UUID id, PageRequest pageRequest) {
        log.debug("getCardsByOwner called for userId={} page={} size={}", id, pageRequest.getPageNumber(), pageRequest.getPageSize());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        Page<Card> page = cardRepository.findAllByUser(user, pageRequest);
        log.info("Returning {} cards for userId={} (page {}, size {})", page.getNumberOfElements(), id, page.getNumber(), page.getSize());
        return page;
    }

    /**
     * Retrieves a card by its ID.
     *
     * @param id The ID of the card to retrieve.
     * @return The Card entity.
     * @throws CardNotFoundException if the card with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public Card getById(Long id) {
        log.debug("getById called for cardId={}", id);
        return cardRepository.findById(id).orElseThrow(() -> {
            log.warn("Card {} not found", id);
            return new CardNotFoundException(id);
        });
    }

    /**
     * Retrieves a paginated list of all cards in the system.
     *
     * @param pageRequest The pagination information.
     * @return A Page of Card entities.
     */
    @Transactional(readOnly = true)
    public Page<Card> getAllCards(PageRequest pageRequest) {
        log.debug("getAllCards called page={} size={}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<Card> page = cardRepository.findAll(pageRequest);
        log.info("Returning {} cards (page {}, size {})", page.getNumberOfElements(), page.getNumber(), page.getSize());
        return page;
    }

    /**
     * Transfers a specified amount from one card to another.
     *
     * @param request The transfer request containing source card ID, destination card ID, and amount.
     * @param userId The ID of the user initiating the transfer.
     * @throws CardNotFoundException if either the source or destination card is not found.
     * @throws IsNotOwnerException if the user is not the owner of either the source or destination card.
     */
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

    /**
     * Checks if the given user is the owner of the specified card.
     *
     * @param card The card to check ownership for.
     * @param userId The ID of the user to verify ownership against.
     * @throws IsNotOwnerException if the user is not the owner of the card.
     */
    private void checkOwnership(Card card, UUID userId) {
        if (!card.getUser().getId().equals(userId)) {
            log.warn("User {} is not owner of card {}", userId, card.getId());
            throw new IsNotOwnerException("You are not owner of card " + card.getId());
        }
        log.debug("Ownership validated for user {} and card {}", userId, card.getId());
    }

    /**
     * Retrieves a paginated list of card responses, either all cards (for admin) or cards owned by the authenticated user.
     *
     * @param pageRequest The pagination information.
     * @param auth The authentication object containing user details and roles.
     * @return A Page of CardResponse DTOs.
     */
    @Transactional(readOnly = true)
    public Page<CardResponse> getCards(PageRequest pageRequest, Authentication auth) {
        String userId = JwtPrincipal.getId(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")
        );

        Page<Card> cards;
        if (isAdmin) {
            cards = getAllCards(pageRequest);
        } else {
            cards = getCardsByOwner(UUID.fromString(userId), pageRequest);
        }

        return cards.map(isAdmin
                ? cardMapper::toMaskedResponse
                : cardMapper::toFullResponse
        );
    }

    /**
     * Retrieves a single card response by card ID, with masking based on user ownership and admin status.
     *
     * @param cardId The ID of the card to retrieve.
     * @param auth The authentication object containing user details and roles.
     * @return A CardResponse DTO.
     * @throws IsNotOwnerException if the user is not authorized to view the card.
     */
    @Transactional(readOnly = true)
    public CardResponse getCard(Long cardId, Authentication auth) {
        Card card = getById(cardId);
        UUID userId = UUID.fromString(JwtPrincipal.getId(auth));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = card.getUser().getId().equals(userId);

        if (isOwner) {
            return cardMapper.toFullResponse(card);
        } else if (isAdmin) {
            return cardMapper.toMaskedResponse(card);
        } else {
            throw new IsNotOwnerException("You are not authorized to view this card.");
        }
    }

    /**
     * Creates a new card for a given user account and returns a masked card response.
     *
     * @param userId The ID of the user for whom the card is to be created.
     * @return A masked CardResponse DTO for the newly created card.
     */
    @Transactional
    public CardResponse createCardForAccountAndGetMaskedResponse(UUID userId) {
        Card card = createCardForAccount(userId);
        return cardMapper.toMaskedResponse(card);
    }
}