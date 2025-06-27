package com.example.banking.domain;

import com.example.shared.domain.DomainEvent;

import java.math.BigDecimal;

public class DepositMadeEvent extends DomainEvent {
    private final AccountId accountId;
    private final BigDecimal amount;
    private final BigDecimal newBalance;

    public DepositMadeEvent(AccountId accountId, BigDecimal amount, BigDecimal newBalance) {
        super();
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public AccountId getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getNewBalance() { return newBalance; }
}