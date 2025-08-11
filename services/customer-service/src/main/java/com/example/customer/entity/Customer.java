package com.example.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    UUID id;

    @Column(name = "external_id")
    UUID external_id;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "phone", nullable = true, unique = true)
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
