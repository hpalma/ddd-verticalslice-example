package com.example.banking.infrastructure;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.BankAccount;
import com.example.banking.domain.BankAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaBankAccountRepository implements BankAccountRepository {
    private final SpringDataBankAccountRepository springDataRepository;
    private final BankAccountMapper bankAccountMapper;

    public JpaBankAccountRepository(SpringDataBankAccountRepository springDataRepository, 
                                  BankAccountMapper bankAccountMapper) {
        this.springDataRepository = springDataRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public void save(BankAccount account) {
        BankAccountEntity entity = bankAccountMapper.toEntity(account);
        
        // Set up bidirectional relationship for transactions
        for (var transaction : entity.getTransactions()) {
            transaction.setAccount(entity);
        }
        
        springDataRepository.save(entity);
    }

    @Override
    public Optional<BankAccount> findById(AccountId accountId) {
        return springDataRepository.findById(accountId.value())
                .map(bankAccountMapper::toDomainWithReconstruction);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return springDataRepository.findByAccountNumber(accountNumber)
                .map(bankAccountMapper::toDomainWithReconstruction);
    }
}