package com.example.banking.domain;

import com.example.shared.domain.AggregateRoot;
import com.example.shared.domain.BusinessRule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BankAccount aggregate demonstrating proper use of AggregateRoot.
 * This aggregate enforces business rules, manages transactions as a unit,
 * and maintains consistency across multiple related entities.
 */
public class BankAccount extends AggregateRoot<AccountId> {
    private final String accountNumber;
    private final String accountHolderName;
    private BigDecimal balance;
    private AccountStatus status;
    private final List<Transaction> transactions = new ArrayList<>();
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal totalWithdrawnToday;
    private final LocalDateTime createdAt;

    public BankAccount(AccountId id, String accountNumber, String accountHolderName, 
                      BigDecimal initialDeposit, BigDecimal dailyWithdrawalLimit) {
        super(id);
        
        // Business rules validation during creation
        checkBusinessRule(new MinimumInitialDepositRule(initialDeposit));
        checkBusinessRule(new ValidWithdrawalLimitRule(dailyWithdrawalLimit));
        
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.status = AccountStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.totalWithdrawnToday = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();

        deposit(initialDeposit, "Initial deposit");
        // Domain events will be published by the application service
    }

    // Public method that enforces aggregate boundaries
    public void withdraw(BigDecimal amount, String description) {
        beforeStateChange(); // Validates invariants and marks as modified
        
        // Business rules validation
        checkBusinessRule(new AccountMustBeActiveRule(status));
        checkBusinessRule(new SufficientBalanceRule(balance, amount));
        checkBusinessRule(new WithinDailyLimitRule(totalWithdrawnToday, amount, dailyWithdrawalLimit));
        checkBusinessRule(new MinimumWithdrawalAmountRule(amount));
        
        // Perform the operation as a single unit
        this.balance = this.balance.subtract(amount);
        this.totalWithdrawnToday = this.totalWithdrawnToday.add(amount);
        
        // Add transaction - this is internal to the aggregate
        Transaction transaction = new Transaction(
            TransactionId.generate(),
            TransactionType.WITHDRAWAL,
            amount,
            description,
            this.balance
        );
        this.transactions.add(transaction);
        
        // Domain event will be published by the application service
    }

    public void deposit(BigDecimal amount, String description) {
        beforeStateChange();
        
        checkBusinessRule(new AccountMustBeActiveRule(status));
        checkBusinessRule(new MinimumDepositAmountRule(amount));
        
        this.balance = this.balance.add(amount);
        
        Transaction transaction = new Transaction(
            TransactionId.generate(),
            TransactionType.DEPOSIT,
            amount,
            description,
            this.balance
        );
        this.transactions.add(transaction);
        
        // Domain event will be published by the application service
    }

    public void freeze() {
        beforeStateChange();
        
        checkBusinessRule(new AccountMustBeActiveRule(status));
        
        this.status = AccountStatus.FROZEN;
        // Domain event will be published by the application service
    }

    // Aggregate boundary protection - transactions can only be accessed, not modified
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // Business invariants that must ALWAYS be true for this aggregate
    @Override
    protected void validateAggregateInvariants() {
        // These should never be violated if our business rules are correct
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Account balance cannot be negative");
        }
        
        if (totalWithdrawnToday.compareTo(dailyWithdrawalLimit) > 0) {
            throw new IllegalStateException("Daily withdrawal limit has been exceeded");
        }
        
        // Ensure transaction history consistency
        BigDecimal calculatedBalance = calculateBalanceFromTransactions();
        if (balance.compareTo(calculatedBalance) != 0) {
            throw new IllegalStateException("Balance inconsistency detected");
        }
    }

    private BigDecimal calculateBalanceFromTransactions() {
        return transactions.stream()
                .map(t -> t.getType() == TransactionType.DEPOSIT ? 
                     t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Business Rules as inner classes
    private record MinimumInitialDepositRule(BigDecimal amount) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return amount.compareTo(new BigDecimal("100.00")) >= 0;
        }

        @Override
        public String getMessage() {
            return "Initial deposit must be at least $100.00";
        }
    }

    private record SufficientBalanceRule(BigDecimal currentBalance, BigDecimal withdrawalAmount) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return currentBalance.compareTo(withdrawalAmount) >= 0;
        }

        @Override
        public String getMessage() {
            return "Insufficient balance for withdrawal";
        }
    }

    private record WithinDailyLimitRule(BigDecimal withdrawnToday, BigDecimal withdrawalAmount, 
                                       BigDecimal dailyLimit) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return withdrawnToday.add(withdrawalAmount).compareTo(dailyLimit) <= 0;
        }

        @Override
        public String getMessage() {
            return "Withdrawal would exceed daily limit";
        }
    }

    private record AccountMustBeActiveRule(AccountStatus status) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return status == AccountStatus.ACTIVE;
        }

        @Override
        public String getMessage() {
            return "Account must be active to perform this operation";
        }
    }

    private record MinimumWithdrawalAmountRule(BigDecimal amount) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return amount.compareTo(new BigDecimal("1.00")) >= 0;
        }

        @Override
        public String getMessage() {
            return "Minimum withdrawal amount is $1.00";
        }
    }

    private record MinimumDepositAmountRule(BigDecimal amount) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return amount.compareTo(new BigDecimal("0.01")) >= 0;
        }

        @Override
        public String getMessage() {
            return "Minimum deposit amount is $0.01";
        }
    }

    private record ValidWithdrawalLimitRule(BigDecimal limit) implements BusinessRule {
        @Override
        public boolean isSatisfied() {
            return limit.compareTo(new BigDecimal("1000.00")) >= 0 && 
                   limit.compareTo(new BigDecimal("10000.00")) <= 0;
        }

        @Override
        public String getMessage() {
            return "Daily withdrawal limit must be between $1,000 and $10,000";
        }
    }

    // Static factory method for reconstruction from persistence (bypasses business rules)
    public static BankAccount reconstruct(AccountId id, String accountNumber, String accountHolderName,
                                        BigDecimal balance, AccountStatus status, BigDecimal dailyWithdrawalLimit,
                                        BigDecimal totalWithdrawnToday, LocalDateTime createdAt,
                                        LocalDateTime lastModified, Long version, List<Transaction> transactions) {
        return new BankAccount(id, accountNumber, accountHolderName, balance, status, 
                              dailyWithdrawalLimit, totalWithdrawnToday, createdAt, 
                              lastModified, version, transactions);
    }

    // Private constructor for reconstruction from persistence
    private BankAccount(AccountId id, String accountNumber, String accountHolderName,
                       BigDecimal balance, AccountStatus status, BigDecimal dailyWithdrawalLimit,
                       BigDecimal totalWithdrawnToday, LocalDateTime createdAt,
                       LocalDateTime lastModified, Long version, List<Transaction> transactions) {
        super(id);
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.status = status;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.totalWithdrawnToday = totalWithdrawnToday;
        this.createdAt = createdAt;
        this.transactions.addAll(transactions);
        
        // Set version and last modified directly since this is reconstruction
        setVersion(version);
        setLastModified(lastModified);
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public BigDecimal getDailyWithdrawalLimit() { return dailyWithdrawalLimit; }
    public BigDecimal getTotalWithdrawnToday() { return totalWithdrawnToday; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}