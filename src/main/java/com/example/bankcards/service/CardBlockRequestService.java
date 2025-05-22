package com.example.bankcards.service;

import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.repository.CardBlockRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class CardBlockRequestService {

    private final CardBlockRequestRepository cardBlockRequestRepository;

    public CardBlockRequestService(CardBlockRequestRepository cardBlockRequestRepository) {
        this.cardBlockRequestRepository = cardBlockRequestRepository;
    }

    public void createBlockRequest(CardBlockRequest cardBlockRequest) {
        cardBlockRequestRepository.save(cardBlockRequest);
    }
}
