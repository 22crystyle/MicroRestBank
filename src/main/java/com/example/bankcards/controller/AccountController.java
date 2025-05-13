package com.example.bankcards.controller;

import com.example.bankcards.dto.AccountDto;
import com.example.bankcards.dto.mapper.AccountMapper;
import com.example.bankcards.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;

    public AccountController(AccountService service, AccountMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto dto) {
        
    }
}
