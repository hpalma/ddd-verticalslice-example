package com.example.banking.application;

import com.example.banking.domain.*;
import com.example.shared.domain.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class BankAccountApplicationService {
    private final BankAccountRepository accountRepository;
    private final DomainEventPublisher eventPublisher;

    public BankAccountApplicationService(BankAccountRepository accountRepository, 
                                       DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    public AccountId openAccount(String accountNumber, String accountHolderName, 
                               BigDecimal initialDeposit, BigDecimal dailyWithdrawalLimit) {
        AccountId accountId = AccountId.generate();
        
        BankAccount account = new BankAccount(
            accountId, 
            accountNumber, 
            accountHolderName, 
            initialDeposit, 
            dailyWithdrawalLimit
        );
        
        accountRepository.save(account);
        
        // Publish domain event via Spring
        eventPublisher.publishEvent(new AccountOpenedEvent(
            accountId, 
            accountNumber, 
            accountHolderName, 
            initialDeposit
        ));
        
        return accountId;
    }

    public void withdraw(AccountId accountId, BigDecimal amount, String description) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        BigDecimal balanceBeforeWithdrawal = account.getBalance();
        account.withdraw(amount, description);
        accountRepository.save(account);
        
        // Publish domain event via Spring
        eventPublisher.publishEvent(new WithdrawalMadeEvent(
            accountId, 
            amount, 
            account.getBalance()
        ));
    }

    public void deposit(AccountId accountId, BigDecimal amount, String description) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        account.deposit(amount, description);
        accountRepository.save(account);
        
        // Publish domain event via Spring
        eventPublisher.publishEvent(new DepositMadeEvent(
            accountId, 
            amount, 
            account.getBalance()
        ));
    }

    public void freezeAccount(AccountId accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        account.freeze();
        accountRepository.save(account);
        
        // Publish domain event via Spring
        eventPublisher.publishEvent(new AccountFrozenEvent(accountId));
    }
}