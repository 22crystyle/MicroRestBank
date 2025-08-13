package com.example.bankcards.service;

import com.example.bankcards.dto.UserMapper;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Transactional
    public User createUser(UserRequest request) {
        User user = mapper.toEntity(request);
        return repository.save(user);
    }

    @Transactional
    public boolean deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id.toString());
        }
        repository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<User> getPage(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }
}
