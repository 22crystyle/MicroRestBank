package org.restbank.service.customer.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for customer responses, extending RepresentationModel for HATEOAS support.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public final class CustomerResponse extends RepresentationModel<CustomerResponse> {
    /**
     * The unique identifier of the customer.
     */
    private final UUID id;
    /**
     * The first name of the customer.
     */
    private final String firstName;
    /**
     * The last name of the customer.
     */
    private final String lastName;
    /**
     * The timestamp when the customer record was created.
     */
    private final Instant created_at; //TODO: SonarQube
}
