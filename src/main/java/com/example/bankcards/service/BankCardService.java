package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.repository.CardRepository;
import org.springframework.stereotype.Service;

@Service
public class BankCardService {

    private final CardRepository cardRepository;

    public BankCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public BankCard save(BankCard bankCard) {
        return cardRepository.save(bankCard);
    }

    public BankCard findById(int id) {
        return cardRepository.findById(id).orElse(null);
    }
}
