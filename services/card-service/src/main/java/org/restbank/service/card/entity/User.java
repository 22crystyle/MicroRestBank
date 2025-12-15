package org.restbank.service.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restbank.libs.api.dto.event.CustomerStatus;

import java.util.UUID;

/**
 * Represents a user entity within the card service.
 *
 * <p>This class is mapped to the "users" table and stores the basic information about a user,
 * including their unique identifier and current status. This entity is synchronized with the
 * customer service through events.</p>
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * The unique identifier for the user, which corresponds to the ID in the customer service.
     */
    @Id
    private UUID id;

    /**
     * The current status of the user, such as ACTIVE or SUSPENDED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status;
}
