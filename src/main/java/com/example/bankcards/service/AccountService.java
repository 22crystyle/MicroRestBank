package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<AccountResponse> createAccount(AccountRequest request) {
        Account account = mapper.toEntity(request);
        Role defaultRole = roleRepository.findById(request.role_id())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        account.setPassword(encoder.encode(account.getPassword()));
        account.setRole(defaultRole);
        return Optional.of(mapper.toResponse(repository.save(account)));
    }
}
