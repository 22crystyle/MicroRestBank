package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card_statuses")
@Data
public class CardStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatusType status;
    private String description;

    public boolean is(CardStatusType type) {
        return getStatus().equals(type);
    }
}