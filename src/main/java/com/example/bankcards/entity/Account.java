package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "accounts")
@JsonIgnoreProperties("bank_cards")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<Card> bank_cards;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
