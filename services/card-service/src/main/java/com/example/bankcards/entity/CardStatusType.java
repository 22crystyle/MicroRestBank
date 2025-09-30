package com.example.bankcards.entity;

/**
 * An enumeration representing the possible statuses of a bank card.
 */
public enum CardStatusType {
    /**
     * The card is active and can be used for transactions.
     */
    ACTIVE,
    /**
     * The card is blocked and cannot be used for transactions.
     */
    BLOCKED,
    /**
     * The card has expired and is no longer valid.
     */
    EXPIRED
}
