package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class CardStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true, nullable = false)
    private String status;
    private String description;
    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY
    )
    private List<Card> cards;
}