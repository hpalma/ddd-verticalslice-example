package com.example.banking.infrastructure;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.BankAccount;
import com.example.banking.domain.Transaction;
import com.example.banking.domain.TransactionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "accountIdToString")
    @Mapping(source = "transactions", target = "transactions", qualifiedByName = "transactionsToEntities")
    BankAccountEntity toEntity(BankAccount account);

    @Named("accountIdToString")
    default String accountIdToString(AccountId accountId) {
        return accountId.value();
    }

    @Named("stringToAccountId")
    default AccountId stringToAccountId(String id) {
        return AccountId.of(id);
    }

    @Named("transactionsToEntities")
    default List<TransactionEntity> transactionsToEntities(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::transactionToEntity)
                .toList();
    }

    @Named("transactionEntitiesToDomain")
    default List<Transaction> transactionEntitiesToDomain(List<TransactionEntity> transactions) {
        return transactions.stream()
                .map(this::transactionEntityToDomain)
                .toList();
    }

    @Mapping(target = "account", ignore = true)
    @Mapping(source = "id", target = "id", qualifiedByName = "transactionIdToString")
    TransactionEntity transactionToEntity(Transaction transaction);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToTransactionId")
    Transaction transactionEntityToDomain(TransactionEntity transactionEntity);

    @Named("transactionIdToString")
    default String transactionIdToString(TransactionId transactionId) {
        return transactionId.value();
    }

    @Named("stringToTransactionId")
    default TransactionId stringToTransactionId(String id) {
        return new TransactionId(id);
    }


    default BankAccount toDomainWithReconstruction(BankAccountEntity entity) {
        // We need to reconstruct the aggregate using reflection or a special constructor
        // since the domain constructor enforces business rules
        return BankAccount.reconstruct(
                AccountId.of(entity.getId()),
                entity.getAccountNumber(),
                entity.getAccountHolderName(),
                entity.getBalance(),
                entity.getStatus(),
                entity.getDailyWithdrawalLimit(),
                entity.getTotalWithdrawnToday(),
                entity.getCreatedAt(),
                entity.getLastModified(),
                entity.getVersion(),
                transactionEntitiesToDomain(entity.getTransactions())
        );
    }
}