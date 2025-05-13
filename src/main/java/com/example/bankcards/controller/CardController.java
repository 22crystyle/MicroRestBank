package com.example.bankcards.controller;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.service.CardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/cards")
public class CardController {

    private final CardMapper cardMapper;
    private final CardService cardService;

    public CardController(CardMapper cardMapper, CardService cardService) {
        this.cardMapper = cardMapper;
        this.cardService = cardService;
    }
}
