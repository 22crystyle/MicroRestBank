package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "BankCard")
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "card_number")
    private Long cardNumber;
    private String owner;
    @Column(name = "expiry_date")
    private YearMonth expiryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id")
    private CardStatus status;
    private BigDecimal balance;
}
