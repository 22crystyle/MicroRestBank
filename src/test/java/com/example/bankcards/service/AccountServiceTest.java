package com.example.bankcards.service;

import com.example.bankcards.dto.AccountMapper;
import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository repository;

    @Mock
    AccountMapper mapper;

    @Mock
    PasswordEncoder encoder;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    private AccountService service;

    @Test
    void whenValidRequest_thenReturnAccountResponse() {
        AccountRequest request = new AccountRequest("user", "pass", "first", "last", "e@mail", "+100", 1);
        Account entity = new Account();
        entity.setId(1L);
        entity.setPassword("pass");
        entity.setFirstName("first");
        entity.setLastName("last");
        entity.setEmail("e@mail");
        entity.setPhone("+100");

        Role role = new Role();
        role.setId(1);
        role.setName("USER");
        entity.setRole(role);

        AccountResponse response = new AccountResponse("user", "first", "last", "e@mail", "+100");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repository.saveAndFlush(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        Optional<AccountResponse> result = service.createAccount(request);

        assertTrue(result.isPresent());
        assertEquals("user", result.get().username(), "Username should match");
        verify(roleRepository).findById(1);
        verify(encoder).encode("pass");
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void whenInvalidRequest_thenReturnAccountResponse() {
        AccountRequest request = new AccountRequest("u", "p", "F", "L", "e", "ph", 99);

        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.createAccount(request),
                "Should throw when role not found");
    }
}
