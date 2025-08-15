package com.example.bankcards.entity;

import com.example.shared.dto.event.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "Users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status;
}
