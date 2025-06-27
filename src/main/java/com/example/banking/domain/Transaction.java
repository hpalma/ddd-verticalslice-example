package com.example.banking.domain;

import com.example.shared.domain.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity - can only exist within a BankAccount aggregate.
 * This demonstrates proper aggregate boundary enforcement.
 */
public class Transaction extends Entity<TransactionId> {
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final BigDecimal balanceAfter;
    private final LocalDateTime timestamp;

    // Package-private constructor - only BankAccount can create transactions
    public Transaction(TransactionId id, TransactionType type, BigDecimal amount,
               String description, BigDecimal balanceAfter) {
        super(id);
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }
}