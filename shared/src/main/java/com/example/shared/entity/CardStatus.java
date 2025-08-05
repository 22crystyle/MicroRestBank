package com.example.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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
    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<Card> cards;

    public CardStatusType getCardStatusType() {
        return CardStatusType.valueOf(name);
    }
}