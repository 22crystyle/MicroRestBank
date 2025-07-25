package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.AccountNotFoundException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.util.data.account.AccountData;
import com.example.bankcards.util.data.account.role.RoleData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountMapper mapper;
    @Mock
    PasswordEncoder encoder;
    @Mock
    RoleRepository roleRepository;
    @Mock
    private AccountRepository repository;
    @InjectMocks
    private AccountService service;

    @Test
    void createAccount_whenValidRequest_thenReturnAccount() {
        AccountRequest request = AccountData.DEFAULT_REQUEST;
        Account entity = AccountData.DEFAULT_ENTITY;
        Role role = RoleData.DEFAULT_ROLE;

        when(mapper.toEntity(request)).thenReturn(entity);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repository.save(entity)).thenReturn(entity);

        Account result = service.createAccount(request);

        assertEquals("user", result.getUsername());
        verify(roleRepository).findById(1);
        verify(encoder).encode("pass");
        verify(repository).save(entity);
    }

    @Test
    void deleteById_whenAccountExists_thenReturnTrue() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        boolean result = service.deleteById(id);

        assertTrue(result);
        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deleteById_whenAccountNotExists_thenReturnFalse() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(false);

        boolean result = service.deleteById(id);

        assertFalse(result);
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    void getAccountById_whenAccountExists_thenReturnAccount() {
        Long id = 1L;
        Account account = AccountData.DEFAULT_ENTITY;
        when(repository.findById(id)).thenReturn(Optional.of(account));

        Account result = service.getAccountById(id);

        assertEquals(account, result);
        verify(repository).findById(id);
    }

    @Test
    void getAccountById_whenAccountNotExists_thenThrowException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.getAccountById(id));
        verify(repository).findById(id);
    }

    @Test
    void getPage_whenCalled_thenReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Account account = AccountData.DEFAULT_ENTITY;
        Page<Account> page = new PageImpl<>(List.of(account));
        when(repository.findAll(pageRequest)).thenReturn(page);

        Page<Account> result = service.getPage(pageRequest);

        assertEquals(page, result);
        verify(repository).findAll(pageRequest);
    }
}
