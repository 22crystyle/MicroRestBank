package com.example.bankcards.entity;

import com.example.bankcards.dto.response.AccountResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank
    private String role;
    private String description;

    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private List<Account> account;
}
