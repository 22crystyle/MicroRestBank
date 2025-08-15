package com.example.bankcards.service;

import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class CardExpiryService {

    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;

    public CardExpiryService(CardRepository cardRepository, CardStatusRepository cardStatusRepository) {
        this.cardRepository = cardRepository;
        this.cardStatusRepository = cardStatusRepository;
    }

    /*@Scheduled(cron = "0 * * * * *")
    public void markExpiredCards() {
        YearMonth now = YearMonth.now();
        CardStatus expiredStatus = cardStatusRepository.findByName("EXPIRED")
                .orElseThrow(() -> new IllegalStateException("EXPIRED status not found"));
        List<Card> toExpire = cardRepository.findByExpiryDate(now);
        toExpire.forEach(card -> card.setStatus(expiredStatus));
        cardRepository.saveAll(toExpire);
    }*/
}
