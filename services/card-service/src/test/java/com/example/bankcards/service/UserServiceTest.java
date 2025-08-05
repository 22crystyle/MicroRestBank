package com.example.bankcards.service;

import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.util.data.user.UserData;
import com.example.bankcards.util.data.user.role.RoleData;
import com.example.shared.dto.UserMapper;
import com.example.shared.dto.request.UserRequest;
import com.example.shared.entity.Role;
import com.example.shared.entity.User;
import com.example.shared.repository.RoleRepository;
import com.example.shared.repository.UserRepository;
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
public class UserServiceTest {
    @Mock
    UserMapper mapper;
    @Mock
    PasswordEncoder encoder;
    @Mock
    RoleRepository roleRepository;
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserService service;

    @Test
    void createUser_whenValidRequest_thenReturnUser() {
        UserRequest request = UserData.DEFAULT_REQUEST;
        User entity = UserData.DEFAULT_ENTITY;
        Role role = RoleData.DEFAULT_ROLE;

        when(mapper.toEntity(request)).thenReturn(entity);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repository.save(entity)).thenReturn(entity);

        User result = service.createUser(request);

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
    void deleteById_whenAccountNotExists_thenThrowAccountNotFoundException() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.deleteById(id));

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        Long id = 1L;
        User user = UserData.DEFAULT_ENTITY;
        when(repository.findById(id)).thenReturn(Optional.of(user));

        User result = service.getUserById(id);

        assertEquals(user, result);
        verify(repository).findById(id);
    }

    @Test
    void getUserById_whenUserNotExists_thenThrowException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getUserById(id));
        verify(repository).findById(id);
    }

    @Test
    void getPage_whenCalled_thenReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        User user = UserData.DEFAULT_ENTITY;
        Page<User> page = new PageImpl<>(List.of(user));
        when(repository.findAll(pageRequest)).thenReturn(page);

        Page<User> result = service.getPage(pageRequest);

        assertEquals(page, result);
        verify(repository).findAll(pageRequest);
    }
}
