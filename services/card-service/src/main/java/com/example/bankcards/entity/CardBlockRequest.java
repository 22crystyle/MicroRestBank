package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a request to block a bank card.
 *
 * <p>This entity is mapped to the "card_block_requests" table and stores information
 * about a request to block a {@link Card}. It includes details such as the card to be blocked,
 * the timestamp of the request, and the status of the request (e.g., PENDING, APPROVED, REJECTED).</p>
 */
@Entity
@Table(name = "card_block_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBlockRequest {

    /**
     * The unique identifier for the block request.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The card that is the subject of the block request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    /**
     * The timestamp when the block request was created.
     */
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * The timestamp when the block request was processed (approved or rejected).
     */
    @Column(name = "processed_at")
    private Instant processedAt;

    /**
     * The unique identifier of the administrator who processed the request.
     */
    @Column(name = "processed_by")
    private UUID processedBy;

    /**
     * The current status of the block request.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Status status;

    /**
     * An enumeration representing the possible statuses of a card block request.
     */
    public enum Status {
        /**
         * The request has been submitted but not yet reviewed.
         */
        PENDING,
        /**
         * The request has been approved, and the card has been blocked.
         */
        APPROVED,
        /**
         * The request has been rejected, and the card remains active.
         */
        REJECTED,
    }
}
