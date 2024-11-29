package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.model.Transaction;
import com.dlvb.asyncbankapp.repository.AccountRepository;
import com.dlvb.asyncbankapp.repository.TransactionRepository;
import com.dlvb.asyncbankapp.service.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final TransactionRepository transactionRepository;

    @Transactional
    public CompletableFuture<Transaction> processTransaction(String fromAccountId, String toAccountId, double amount) {
        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow();

        double fromAccountBalanceAfterTransaction = fromAccount.getBalance() - amount;
        if (fromAccountBalanceAfterTransaction >= 0) {
            fromAccount.setBalance(fromAccountBalanceAfterTransaction);
            toAccount.setBalance(toAccount.getBalance() + amount);

        }

        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .build();

        transactionRepository.save(transaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return CompletableFuture.completedFuture(transaction);
    }

    @Transactional
    public void processMultipleTransactions(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            processTransaction(transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> processMultipleTransactionsAsync(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            processTransaction(transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
        }
        return CompletableFuture.completedFuture(null);
    }

}
