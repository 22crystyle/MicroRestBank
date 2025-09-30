package com.example.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for customer creation and update requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CustomerRequest {
    /**
     * The unique identifier of the customer. Optional for creation.
     */
    private UUID id;
    /**
     * The username of the customer.
     */
    private String username;
    /**
     * The first name of the customer.
     */
    private String firstName;
    /**
     * The last name of the customer.
     */
    private String lastName;
    /**
     * The email address of the customer.
     */
    private String email;
}
