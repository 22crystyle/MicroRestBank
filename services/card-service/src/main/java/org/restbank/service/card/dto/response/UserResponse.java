package org.restbank.service.card.dto.response;

import org.restbank.libs.api.dto.event.CustomerStatus;

import java.util.UUID;

/**
 * Represents the response data for a user associated with a bank card.
 *
 * <p>This record contains the user's unique identifier and their current status.</p>
 *
 * @param id     The unique identifier (UUID) of the user.
 * @param status The current status of the user (e.g., ACTIVE, SUSPENDED).
 */
public record UserResponse(
        UUID id,
        CustomerStatus status
) {
}