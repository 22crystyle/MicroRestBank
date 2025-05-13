package com.example.bankcards.service;

import com.example.bankcards.entity.Account;
import com.example.bankcards.repository.AccountRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account save(Account account) {
        return repository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return repository.findById(id);
    }

}
