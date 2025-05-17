package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "card_statuses")
public class CardStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY
    )
    private List<Card> cards;
}