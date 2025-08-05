package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "card_block_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private com.example.entity.Card card;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Status status;

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
    }
}
