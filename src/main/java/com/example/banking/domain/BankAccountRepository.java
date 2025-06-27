package com.example.banking.domain;

import java.util.Optional;

public interface BankAccountRepository {
    void save(BankAccount account);
    Optional<BankAccount> findById(AccountId accountId);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}