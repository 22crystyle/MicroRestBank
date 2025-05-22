package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "card_block_requests")
public class CardBlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Status status;

    private enum Status {
        PENDING,
        APPROVED,
        REJECTED,
    }
}
