package com.example.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CustomerRequest {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
