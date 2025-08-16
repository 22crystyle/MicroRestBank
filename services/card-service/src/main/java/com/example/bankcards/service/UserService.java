package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.shared.dto.event.CustomerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void applyCustomerCreated(CustomerCreatedEvent event) {
        User user = new User();
        if (event.getId() != null) {
            user.setId(event.getId());
        } else {
            user.setId(UUID.randomUUID());
        }
        user.setStatus(event.getStatus());
        userRepository.save(user);
    }
}
