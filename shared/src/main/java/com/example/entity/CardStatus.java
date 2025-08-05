package com.example.entity;

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
    private List<com.example.entity.Card> cards;

    public com.example.entity.CardStatusType getCardStatusType() {
        return com.example.entity.CardStatusType.valueOf(name);
    }
}