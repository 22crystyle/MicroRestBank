package com.example.bankcards.service;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AccountRepository accountRepository;
    private final CardStatusRepository cardStatusRepository;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, AccountRepository accountRepository, CardStatusRepository cardStatusRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.accountRepository = accountRepository;
        this.cardStatusRepository = cardStatusRepository;
    }

    public Card findById(int id) {
        return cardRepository.findById(id).orElse(null);
    }

    public Optional<Card> createCardForAccount(Long accountId) {
        Card card = new Card();
        card.setCardNumber("");
        card.setOwner(accountRepository.findById(accountId).orElseThrow(
                () -> new UsernameNotFoundException("UserName not found by id")
        ));
        card.setExpiryDate(YearMonth.now().plusYears(4));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(cardStatusRepository.findById(1).orElseThrow(
                () -> new IllegalArgumentException("Unknown status")
        ));
        return Optional.of(cardRepository.save(card));
    }
}
