package com.example.bankcards.dto.request;

import java.math.BigDecimal;

/**
 * Represents a request to transfer funds between two bank cards.
 *
 * <p>This record encapsulates the necessary information for a fund transfer, including the
 * source card ID, the destination card ID, and the amount to be transferred.</p>
 *
 * @param fromCardId The ID of the card from which the funds will be withdrawn.
 * @param toCardId   The ID of the card to which the funds will be deposited.
 * @param amount     The amount of money to be transferred.
 */
public record TransferRequest(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
