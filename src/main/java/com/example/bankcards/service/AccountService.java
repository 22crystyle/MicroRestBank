package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public AccountService(AccountRepository repository, AccountMapper mapper, PasswordEncoder encoder, RoleRepository roleRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    public Account createAccount(AccountRequest request) {
        Account account = mapper.toEntity(request);
        Role defaultRole = roleRepository.findById(request.role_id())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        account.setPassword(encoder.encode(request.password()));
        account.setRole(defaultRole);
        repository.save(account);
        return account;
    }

    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public Account getAccountById(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    public Page<Account> getAllAccounts(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }
}
