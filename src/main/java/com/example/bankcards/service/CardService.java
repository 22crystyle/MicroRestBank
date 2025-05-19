package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static com.example.bankcards.util.MastercardGenerator.generateMastercardNumber;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardStatusRepository cardStatusRepository;

    public CardService(CardRepository cardRepository, AccountRepository accountRepository, CardStatusRepository cardStatusRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.cardStatusRepository = cardStatusRepository;
    }

    public Card createCardForAccount(Long accountId) {
        Card card = new Card();
        String number;
        do {
            number = generateMastercardNumber();
        } while (cardRepository.existsCardByCardNumber(number));
        card.setCardNumber(number);
        card.setOwner(accountRepository.findById(accountId).orElseThrow(
                () -> new UsernameNotFoundException("UserName not found by id")
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(1).orElseThrow(
                () -> new IllegalArgumentException("Unknown status")
        ));
        return cardRepository.save(card);
    }

    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.getCardsByOwnerId(userId);
    }
}
