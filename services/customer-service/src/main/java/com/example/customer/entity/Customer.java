package com.example.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "external_id")
    UUID external_id;

    @Column(name = "first_name", nullable = false)
    String first_name;

    @Column(name = "last_name", nullable = false)
    String last_name;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "phone", nullable = false, unique = true)
    String phone;

    @Column(name = "status")
    CustomerStatus status;

    @Column(name = "date_of_birth")
    Date date_of_birth;

    @Column(name = "created_at")
    Instant created_at;

    @Column(name = "updated_at")
    Instant updated_at;
}
