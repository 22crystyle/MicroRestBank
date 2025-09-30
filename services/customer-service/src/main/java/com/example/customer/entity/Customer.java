package com.example.customer.entity;

import com.example.shared.dto.event.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a customer entity in the database.
 */
@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    /**
     * The unique identifier for the customer.
     */
    @Id
    UUID id;

    /**
     * The first name of the customer.
     */
    @Column(name = "first_name", nullable = false)
    String firstName;

    /**
     * The last name of the customer.
     */
    @Column(name = "last_name", nullable = false)
    String lastName;

    /**
     * The email address of the customer, which must be unique.
     */
    @Column(name = "email", nullable = false, unique = true)
    String email;

    /**
     * The phone number of the customer, which must be unique if present.
     */
    @Column(name = "phone", nullable = true, unique = true)
    String phone;

    /**
     * The current status of the customer (e.g., ACTIVE, INACTIVE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CustomerStatus status;

    /**
     * The date of birth of the customer.
     */
    @Column(name = "date_of_birth")
    Date dateOfBirth;

    /**
     * The timestamp when the customer record was created.
     */
    @Column(name = "created_at")
    Instant createdAt;

    /**
     * The timestamp when the customer record was last updated.
     */
    @Column(name = "updated_at")
    Instant updatedAt;
}
