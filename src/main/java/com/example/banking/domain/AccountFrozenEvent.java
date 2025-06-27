package com.example.banking.domain;

import com.example.shared.domain.DomainEvent;

public class AccountFrozenEvent extends DomainEvent {
    private final AccountId accountId;

    public AccountFrozenEvent(AccountId accountId) {
        super();
        this.accountId = accountId;
    }

    public AccountId getAccountId() { return accountId; }
}