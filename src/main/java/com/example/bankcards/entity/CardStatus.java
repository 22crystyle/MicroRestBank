package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class CardStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true, nullable = false)
    private String status;
    @Column(nullable = false)
    private String description;
    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY
    )
    private List<Card> cards;
}