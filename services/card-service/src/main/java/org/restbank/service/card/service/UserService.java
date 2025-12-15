package org.restbank.service.card.service;

import lombok.RequiredArgsConstructor;
import org.restbank.libs.api.dto.event.CustomerCreatedEvent;
import org.restbank.service.card.entity.User;
import org.restbank.service.card.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing user-related operations within the card service context.
 * Primarily handles the creation of local user representations based on events from other services.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Applies a CustomerCreatedEvent to create a new user in the card service's database.
     * If the event contains an ID, it is used; otherwise, a new UUID is generated.
     *
     * @param event The CustomerCreatedEvent containing details of the newly created customer.
     */
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
