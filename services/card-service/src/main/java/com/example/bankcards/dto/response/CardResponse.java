package com.example.bankcards.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

/**
 * Represents the response data for a bank card.
 *
 * <p>This class extends {@link RepresentationModel} to support HATEOAS, allowing for the inclusion
 * of navigational links in the API response. It contains essential details about a card,
 * such as its ID, PAN (Primary Account Number), associated user, status, and balance.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CardResponse extends RepresentationModel<CardResponse> {
    /**
     * The unique identifier of the card.
     */
    private final Long id;

    /**
     * The Primary Account Number (PAN) of the card. This may be masked for security.
     */
    private final String pan;

    /**
     * The user associated with the card.
     */
    private final UserResponse user;

    /**
     * The current status of the card (e.g., ACTIVE, BLOCKED).
     */
    private final CardStatusResponse status;

    /**
     * The available balance on the card.
     */
    private final BigDecimal balance;
}
