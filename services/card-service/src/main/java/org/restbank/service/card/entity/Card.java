package org.restbank.service.card.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.restbank.service.card.exception.CardIsBlockedException;
import org.restbank.service.card.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Represents a bank card entity.
 *
 * <p>This class is mapped to the "cards" table in the database and contains information
 * about a bank card, including its PAN, owner, expiry date, status, and balance.
 * It also includes methods for performing financial operations like withdrawals and deposits.</p>
 */
@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Card {

    /**
     * The unique identifier for the card.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Primary Account Number (PAN) of the card.
     */
    @Column(name = "pan", length = 16, nullable = false)
    private String pan;

    /**
     * The user who owns the card.
     */
    @JdbcTypeCode(SqlTypes.BINARY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    /**
     * The expiry date of the card, represented as a {@link YearMonth}.
     */
    @Column(name = "expiry_date")
    private YearMonth expiryDate;

    /**
     * The current status of the card (e.g., ACTIVE, BLOCKED).
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id")
    private CardStatus status;

    /**
     * The available balance on the card.
     */
    private BigDecimal balance;

    /**
     * Ensures that the card is not blocked before performing an operation.
     *
     * @throws CardIsBlockedException if the card's status is BLOCKED.
     */
    private void ensureNotBlocked() {
        if (status.is(CardStatusType.BLOCKED)) {
            throw new CardIsBlockedException(id);
        }
    }

    /**
     * Withdraws a specified amount from the card's balance.
     *
     * @param amount The amount to withdraw. Must be a positive value.
     * @throws CardIsBlockedException if the card is blocked.
     * @throws InvalidAmountException if the amount is not positive or if there are insufficient funds.
     */
    public void withdraw(BigDecimal amount) {
        ensureNotBlocked();
        if (amount.signum() <= 0) throw new InvalidAmountException("Amount must be positive.");
        if (balance.compareTo(amount) < 0) throw new InvalidAmountException("Insufficient pounds.");
        balance = balance.subtract(amount);
    }

    /**
     * Deposits a specified amount into the card's balance.
     *
     * @param amount The amount to deposit. Must be a positive value.
     * @throws CardIsBlockedException if the card is blocked.
     * @throws InvalidAmountException if the amount is not positive.
     */
    public void deposit(BigDecimal amount) {
        ensureNotBlocked();
        if (amount.signum() <= 0) throw new InvalidAmountException("Amount must be positive.");
        balance = balance.add(amount);
    }
}
