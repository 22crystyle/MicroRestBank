package com.example.bankcards.util.pan;

/**
 * Interface for generating Payment Account Numbers (PANs) for bank cards.
 */
public interface CardPanGenerator {
    /**
     * Generates a unique Payment Account Number (PAN).
     *
     * @return A string representing the generated card PAN.
     */
    String generateCardPan();
}
