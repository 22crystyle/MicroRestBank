package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Card> bank_cards;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
