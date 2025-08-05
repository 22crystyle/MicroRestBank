package com.example.bankcards.service;

import com.example.bankcards.dto.UserMapper;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.shared.entity.Role;
import com.example.shared.entity.User;
import com.example.shared.repository.RoleRepository;
import com.example.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    @Transactional
    public User createUser(UserRequest request) {
        User user = mapper.toEntity(request);
        Role defaultRole = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RoleNotFoundException(request.roleId()));
        user.setPassword(encoder.encode(request.password()));
        user.setRole(defaultRole);
        return repository.save(user);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<User> getPage(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }
}
