package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bankcard")
public class BankCardController {
    @GetMapping("{id}")
    public String getBankCard(@PathVariable int id) {
        return "Bank card controller";
    }
}
