package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardStatusType;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

/**
 * Service responsible for handling card expiry related operations.
 * This includes a scheduled task to mark expired cards.
 */
/**
 * Service responsible for handling card expiry related operations.
 * This includes a scheduled task to mark expired cards.
 */
@Slf4j
@Service
public class CardExpiryService {

    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;

    public CardExpiryService(CardRepository cardRepository, CardStatusRepository cardStatusRepository) {
        this.cardRepository = cardRepository;
        this.cardStatusRepository = cardStatusRepository;
    }

    /**
     * Scheduled task to mark cards as EXPIRED if their expiry date is in the current month or past.
     * Runs every minute.
     */
    /**
     * Scheduled task to mark cards as EXPIRED if their expiry date is in the current month or past.
     * Runs every minute.
     */
    @Scheduled(cron = "0 * * * * *")
    public void markExpiredCards() {
        log.debug("Starting scheduled task: markExpiredCards");

        YearMonth now = YearMonth.now();
        CardStatus expiredStatus = cardStatusRepository.findByName(CardStatusType.EXPIRED)
                .orElseThrow(() -> {
                    log.error("Card status EXPIRED not found in DB");
                    return new IllegalStateException("EXPIRED status not found");
                });
        List<Card> toExpire = cardRepository.findByExpiryDate(now);
        log.info("Found {} cards expiring at {}", toExpire.size(), now);

        if (!toExpire.isEmpty()) {
            toExpire.forEach(card -> card.setStatus(expiredStatus));
            cardRepository.saveAll(toExpire);
            log.info("Marked {} cards as EXPIRED", toExpire.size());
        } else {
            log.debug("No cards to expire at {}", now);
        }
    }
}
