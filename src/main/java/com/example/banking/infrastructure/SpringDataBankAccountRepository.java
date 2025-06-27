package com.example.banking.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataBankAccountRepository extends JpaRepository<BankAccountEntity, String> {
    Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
}