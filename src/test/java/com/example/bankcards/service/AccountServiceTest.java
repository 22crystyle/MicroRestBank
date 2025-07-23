package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.util.TestDataBuilders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void whenValidRequest_thenReturnAccountResponse() {
        AccountRequest request = TestDataBuilders.accountRequest().build();
        Account entity = TestDataBuilders.account().build();

        Role role = TestDataBuilders.role().build();
        entity.setRole(role);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repository.save(entity)).thenReturn(entity);

        Account result = service.createAccount(request);

        assertEquals("user", result.getUsername(), "Username should match");
        verify(roleRepository).findById(1);
        verify(encoder).encode("pass");
        verify(repository).save(entity);
    }

    @Test
    void whenInvalidRequest_thenReturnAccountResponse() {
        AccountRequest request = TestDataBuilders.accountRequest().withRoleId(99).build();

        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.createAccount(request),
                "Should throw when role not found");
    }
}
