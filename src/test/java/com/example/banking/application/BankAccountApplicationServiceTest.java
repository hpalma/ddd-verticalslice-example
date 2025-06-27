package com.example.banking.application;

import com.example.banking.domain.*;
import com.example.shared.domain.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountApplicationServiceTest {

    @Mock
    private BankAccountRepository accountRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private BankAccountApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new BankAccountApplicationService(accountRepository, eventPublisher);
    }

    @Test
    void openAccount_shouldSaveAccountAndPublishEvent() {
        AccountId accountId = applicationService.openAccount(
            "12345",
            "John Doe",
            new BigDecimal("500.00"),
            new BigDecimal("1000.00")
        );

        assertNotNull(accountId);
        
        // Verify account was saved
        verify(accountRepository).save(any(BankAccount.class));
        
        // Verify event was published
        ArgumentCaptor<AccountOpenedEvent> eventCaptor = ArgumentCaptor.forClass(AccountOpenedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        AccountOpenedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals("12345", publishedEvent.getAccountNumber());
        assertEquals("John Doe", publishedEvent.getAccountHolderName());
        assertEquals(new BigDecimal("500.00"), publishedEvent.getInitialDeposit());
    }

    @Test
    void withdraw_shouldUpdateAccountAndPublishEvent() {
        AccountId accountId = AccountId.generate();
        BankAccount account = new BankAccount(
            accountId,
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        applicationService.withdraw(accountId, new BigDecimal("100.00"), "ATM withdrawal");

        // Verify account was saved
        verify(accountRepository).save(account);
        
        // Verify event was published
        ArgumentCaptor<WithdrawalMadeEvent> eventCaptor = ArgumentCaptor.forClass(WithdrawalMadeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        WithdrawalMadeEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(new BigDecimal("100.00"), publishedEvent.getAmount());
        assertEquals(new BigDecimal("900.00"), publishedEvent.getNewBalance());
    }

    @Test
    void deposit_shouldUpdateAccountAndPublishEvent() {
        AccountId accountId = AccountId.generate();
        BankAccount account = new BankAccount(
            accountId,
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        applicationService.deposit(accountId, new BigDecimal("250.00"), "Direct deposit");

        // Verify account was saved
        verify(accountRepository).save(account);
        
        // Verify event was published
        ArgumentCaptor<DepositMadeEvent> eventCaptor = ArgumentCaptor.forClass(DepositMadeEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        DepositMadeEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(new BigDecimal("250.00"), publishedEvent.getAmount());
        assertEquals(new BigDecimal("1250.00"), publishedEvent.getNewBalance());
    }

    @Test
    void freezeAccount_shouldUpdateAccountAndPublishEvent() {
        AccountId accountId = AccountId.generate();
        BankAccount account = new BankAccount(
            accountId,
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        applicationService.freezeAccount(accountId);

        // Verify account was saved
        verify(accountRepository).save(account);
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        
        // Verify event was published
        ArgumentCaptor<AccountFrozenEvent> eventCaptor = ArgumentCaptor.forClass(AccountFrozenEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        
        AccountFrozenEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
    }
}