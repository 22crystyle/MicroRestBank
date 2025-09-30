package com.example.customer.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public final class CustomerResponse extends RepresentationModel<CustomerResponse> {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Instant created_at;
}
