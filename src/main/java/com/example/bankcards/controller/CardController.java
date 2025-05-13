package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/card")
public class CardController {
    @GetMapping("{id}")
    public String getBankCard(@PathVariable int id) {
        return "Bank card controller";
    }
}
