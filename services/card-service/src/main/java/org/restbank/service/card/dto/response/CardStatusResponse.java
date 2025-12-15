package org.restbank.service.card.dto.response;

/**
 * Represents the response data for a card status.
 *
 * <p>This record provides details about the status of a bank card, including its ID,
 * a human-readable description, and the status name (e.g., ACTIVE, BLOCKED).</p>
 *
 * @param id          The unique identifier for the card status.
 * @param description A brief description of what the status means.
 * @param name        The name of the status.
 */
public record CardStatusResponse(
        Integer id,
        String description,
        String name
) {
}