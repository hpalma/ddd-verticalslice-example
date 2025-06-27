package com.example.banking.application;

import com.example.banking.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BankingEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(BankingEventHandler.class);

    @EventListener
    public void handleAccountOpened(AccountOpenedEvent event) {
        logger.info("Account opened: {} for {} with initial deposit of ${}", 
                   event.getAccountNumber(), 
                   event.getAccountHolderName(), 
                   event.getInitialDeposit());
        
        // Could trigger:
        // - Welcome email
        // - Account setup tasks
        // - Compliance checks
    }

    @EventListener
    public void handleWithdrawal(WithdrawalMadeEvent event) {
        logger.info("Withdrawal of ${} made from account: {}. New balance: ${}", 
                   event.getAmount(), 
                   event.getAccountId().value(), 
                   event.getNewBalance());
        
        // Could trigger:
        // - Fraud detection
        // - Low balance alerts
        // - Transaction notifications
    }

    @EventListener
    public void handleDeposit(DepositMadeEvent event) {
        logger.info("Deposit of ${} made to account: {}. New balance: ${}", 
                   event.getAmount(), 
                   event.getAccountId().value(), 
                   event.getNewBalance());
        
        // Could trigger:
        // - Large deposit monitoring
        // - Interest calculations
        // - Balance notifications
    }

    @EventListener
    public void handleAccountFrozen(AccountFrozenEvent event) {
        logger.warn("Account frozen: {}", event.getAccountId().value());
        
        // Could trigger:
        // - Customer notifications
        // - Compliance reporting
        // - Card deactivation
    }
}