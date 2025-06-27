package com.example.banking.domain;

import com.example.shared.domain.DomainEvent;

import java.math.BigDecimal;

public class AccountOpenedEvent extends DomainEvent {
    private final AccountId accountId;
    private final String accountNumber;
    private final String accountHolderName;
    private final BigDecimal initialDeposit;

    public AccountOpenedEvent(AccountId accountId, String accountNumber, 
                             String accountHolderName, BigDecimal initialDeposit) {
        super();
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.initialDeposit = initialDeposit;
    }

    public AccountId getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
}