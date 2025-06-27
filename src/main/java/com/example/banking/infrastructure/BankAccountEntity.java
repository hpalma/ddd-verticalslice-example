package com.example.banking.infrastructure;

import com.example.banking.domain.AccountStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
public class BankAccountEntity {
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String accountNumber;
    
    @Column(nullable = false)
    private String accountHolderName;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyWithdrawalLimit;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalWithdrawnToday;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime lastModified;
    
    @Column(nullable = false)
    private Long version;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TransactionEntity> transactions = new ArrayList<>();

    protected BankAccountEntity() {}

    public BankAccountEntity(String id, String accountNumber, String accountHolderName, 
                           BigDecimal balance, AccountStatus status, BigDecimal dailyWithdrawalLimit,
                           BigDecimal totalWithdrawnToday, LocalDateTime createdAt, 
                           LocalDateTime lastModified, Long version) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.status = status;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.totalWithdrawnToday = totalWithdrawnToday;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public BigDecimal getTotalWithdrawnToday() {
        return totalWithdrawnToday;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public Long getVersion() {
        return version;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setTotalWithdrawnToday(BigDecimal totalWithdrawnToday) {
        this.totalWithdrawnToday = totalWithdrawnToday;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}