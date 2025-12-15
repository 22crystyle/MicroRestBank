package org.restbank.service.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.restbank.libs.api.dto.event.EventType;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an event stored in the outbox table for reliable event publishing.
 */
@Entity
@Table(name = "outbox")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    /**
     * The unique identifier for the outbox event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The type of the aggregate that generated the event (e.g., "Customer").
     */
    @Column(name = "aggregate_type")
    private String aggregateType;

    /**
     * The ID of the aggregate that generated the event.
     */
    @Column(name = "aggregate_id")
    private String aggregateId;

    /**
     * The type of the event (e.g., CUSTOMER_CREATED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    /**
     * The event payload in JSONB format.
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    /**
     * The timestamp when the event was created.
     */
    @Column(name = "created_at")
    private Instant createdAt;
}
