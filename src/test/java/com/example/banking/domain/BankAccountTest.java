package com.example.banking.domain;

import com.example.shared.domain.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void createAccount_shouldEnforceMinimumDeposit() {
        assertThrows(BusinessRuleViolationException.class, () -> {
            new BankAccount(
                AccountId.generate(),
                "12345",
                "John Doe",
                new BigDecimal("50.00"), // Below minimum of $100
                new BigDecimal("1000.00")
            );
        });
    }

    @Test
    void withdraw_shouldEnforceBusinessRules() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("500.00"),
            new BigDecimal("1000.00")
        );

        // Should work fine
        account.withdraw(new BigDecimal("100.00"), "ATM Withdrawal");
        assertEquals(new BigDecimal("400.00"), account.getBalance());

        // Should fail - insufficient balance
        assertThrows(BusinessRuleViolationException.class, () -> {
            account.withdraw(new BigDecimal("500.00"), "Large withdrawal");
        });
    }

    @Test
    void withdraw_shouldEnforceDailyLimit() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("5000.00"),
            new BigDecimal("1000.00") // Daily limit
        );

        // First withdrawal should work
        account.withdraw(new BigDecimal("600.00"), "First withdrawal");
        
        // Second withdrawal should fail (would exceed daily limit)
        assertThrows(BusinessRuleViolationException.class, () -> {
            account.withdraw(new BigDecimal("500.00"), "Second withdrawal");
        });
    }

    @Test
    void aggregate_shouldMaintainConsistency() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );

        account.withdraw(new BigDecimal("100.00"), "Withdrawal 1");
        account.deposit(new BigDecimal("50.00"), "Deposit 1");
        account.withdraw(new BigDecimal("75.00"), "Withdrawal 2");

        // Balance should be consistent with transaction history
        assertEquals(new BigDecimal("875.00"), account.getBalance());
        assertEquals(4, account.getTransactions().size());
        
        // Aggregate invariants should pass
        assertDoesNotThrow(account::validateAggregateInvariants);
    }

    @Test
    void businessLogic_shouldWorkWithoutEvents() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );

        // Business logic should work correctly
        account.withdraw(new BigDecimal("100.00"), "Test withdrawal");
        assertEquals(new BigDecimal("900.00"), account.getBalance());
        
        account.deposit(new BigDecimal("50.00"), "Test deposit");
        assertEquals(new BigDecimal("950.00"), account.getBalance());
        
        // Domain events are handled by application service, not aggregate
        // This test focuses on business logic only
    }

    @Test
    void version_shouldIncrementOnChanges() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );

        Long initialVersion = account.getVersion();
        
        account.withdraw(new BigDecimal("100.00"), "Test");
        assertTrue(account.getVersion() > initialVersion);
        
        Long afterWithdrawal = account.getVersion();
        account.deposit(new BigDecimal("50.00"), "Test");
        assertTrue(account.getVersion() > afterWithdrawal);
    }

    @Test
    void frozenAccount_shouldRejectOperations() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );

        account.freeze();
        assertEquals(AccountStatus.FROZEN, account.getStatus());

        // Should reject operations on frozen account
        assertThrows(BusinessRuleViolationException.class, () -> {
            account.withdraw(new BigDecimal("100.00"), "Should fail");
        });

        assertThrows(BusinessRuleViolationException.class, () -> {
            account.deposit(new BigDecimal("100.00"), "Should fail");
        });
    }
}