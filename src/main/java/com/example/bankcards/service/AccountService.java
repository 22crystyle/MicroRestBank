package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    @Transactional
    public Account createAccount(AccountRequest request) { // TODO: заранее проверить уникален ли `username` через `repository.existsByUsername(request.username())`
        Account account = mapper.toEntity(request);
        Role defaultRole = roleRepository.findById(request.role_id())
                .orElseThrow(() -> new IllegalArgumentException("Role not found")); // TODO: RoleNotFoundException
        account.setPassword(encoder.encode(request.password()));
        account.setRole(defaultRole);
        return repository.save(account);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Account not found by id: " + id)); // TODO: AccountNotFoundException
    }

    @Transactional(readOnly = true)
    public Page<Account> getAllAccounts(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }
}
