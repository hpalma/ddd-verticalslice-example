package com.example.banking.infrastructure;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.BankAccount;
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
class JpaBankAccountRepositoryTest {

    @Mock
    private SpringDataBankAccountRepository springDataRepository;
    
    @Mock
    private BankAccountMapper bankAccountMapper;
    
    private JpaBankAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaBankAccountRepository(springDataRepository, bankAccountMapper);
    }

    @Test
    void save_shouldMapAndSaveAccountWithTransactions() {
        BankAccount account = new BankAccount(
            AccountId.generate(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        BankAccountEntity mockEntity = new BankAccountEntity(
            account.getId().value(),
            account.getAccountNumber(),
            account.getAccountHolderName(),
            account.getBalance(),
            account.getStatus(),
            account.getDailyWithdrawalLimit(),
            account.getTotalWithdrawnToday(),
            account.getCreatedAt(),
            account.getLastModified(),
            account.getVersion()
        );
        
        when(bankAccountMapper.toEntity(account)).thenReturn(mockEntity);

        repository.save(account);

        ArgumentCaptor<BankAccountEntity> entityCaptor = ArgumentCaptor.forClass(BankAccountEntity.class);
        verify(springDataRepository).save(entityCaptor.capture());
        verify(bankAccountMapper).toEntity(account);
        
        BankAccountEntity savedEntity = entityCaptor.getValue();
        assertEquals(mockEntity, savedEntity);
    }

    @Test
    void findById_shouldMapAndReturnAccount() {
        AccountId accountId = AccountId.generate();
        BankAccountEntity mockEntity = new BankAccountEntity(
            accountId.value(),
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            com.example.banking.domain.AccountStatus.ACTIVE,
            new BigDecimal("2000.00"),
            BigDecimal.ZERO,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now(),
            1L
        );
        
        BankAccount mockAccount = new BankAccount(
            accountId,
            "12345",
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        when(springDataRepository.findById(accountId.value())).thenReturn(Optional.of(mockEntity));
        when(bankAccountMapper.toDomainWithReconstruction(mockEntity)).thenReturn(mockAccount);

        Optional<BankAccount> result = repository.findById(accountId);

        assertTrue(result.isPresent());
        assertEquals(mockAccount, result.get());
        verify(springDataRepository).findById(accountId.value());
        verify(bankAccountMapper).toDomainWithReconstruction(mockEntity);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        AccountId accountId = AccountId.generate();
        
        when(springDataRepository.findById(accountId.value())).thenReturn(Optional.empty());

        Optional<BankAccount> result = repository.findById(accountId);

        assertFalse(result.isPresent());
        verify(springDataRepository).findById(accountId.value());
        verifyNoInteractions(bankAccountMapper);
    }

    @Test
    void findByAccountNumber_shouldMapAndReturnAccount() {
        String accountNumber = "12345";
        BankAccountEntity mockEntity = new BankAccountEntity(
            AccountId.generate().value(),
            accountNumber,
            "John Doe",
            new BigDecimal("1000.00"),
            com.example.banking.domain.AccountStatus.ACTIVE,
            new BigDecimal("2000.00"),
            BigDecimal.ZERO,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now(),
            1L
        );
        
        BankAccount mockAccount = new BankAccount(
            AccountId.of(mockEntity.getId()),
            accountNumber,
            "John Doe",
            new BigDecimal("1000.00"),
            new BigDecimal("2000.00")
        );
        
        when(springDataRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(mockEntity));
        when(bankAccountMapper.toDomainWithReconstruction(mockEntity)).thenReturn(mockAccount);

        Optional<BankAccount> result = repository.findByAccountNumber(accountNumber);

        assertTrue(result.isPresent());
        assertEquals(mockAccount, result.get());
        verify(springDataRepository).findByAccountNumber(accountNumber);
        verify(bankAccountMapper).toDomainWithReconstruction(mockEntity);
    }
}