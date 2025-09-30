package com.example.bankcards.entity;

import com.example.shared.dto.event.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an event that has been processed by the service.
 *
 * <p>This entity is used to track events consumed from a message queue (e.g., Kafka)
 * to ensure that each event is processed exactly once. It stores the event's unique ID,
 * the ID of the aggregate it relates to, the event type, and the timestamp of when it was
 * processed.</p>
 */
@Entity
@Table(name = "processed_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessedEvent {

    /**
     * The unique identifier of the event.
     */
    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    /**
     * The identifier of the aggregate root that the event belongs to (e.g., a customer ID).
     */
    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private String aggregateId;

    /**
     * The type of the event, as defined in the {@link EventType} enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, updatable = false)
    private EventType eventType;

    /**
     * The timestamp indicating when the event was processed.
     */
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
