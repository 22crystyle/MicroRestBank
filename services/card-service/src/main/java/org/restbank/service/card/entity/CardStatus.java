package org.restbank.service.card.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents the status of a bank card (e.g., ACTIVE, BLOCKED, EXPIRED).
 *
 * <p>This entity is mapped to the "card_statuses" table and defines the possible states
 * that a {@link Card} can be in. Each status has a unique name and a description.</p>
 */
@Entity
@Table(name = "card_statuses")
@Data
public class CardStatus {

    /**
     * The unique identifier for the card status.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the status, represented by the {@link CardStatusType} enum.
     */
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatusType name;

    /**
     * A human-readable description of the status.
     */
    private String description;

    /**
     * Checks if the current status is equal to the given {@link CardStatusType}.
     *
     * @param type The {@link CardStatusType} to compare against.
     * @return {@code true} if the names are equal, {@code false} otherwise.
     */
    public boolean is(CardStatusType type) {
        return getName().equals(type);
    }
}