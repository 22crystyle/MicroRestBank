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
    private String name;
    private String description;

    public CardStatusType getCardStatusType() {
        return CardStatusType.valueOf(name);
    }

    public boolean is(CardStatusType type) {
        return getCardStatusType().equals(type);
    }
}