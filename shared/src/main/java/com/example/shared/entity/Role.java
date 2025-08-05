package com.example.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    private String description;

    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<User> users;
}
