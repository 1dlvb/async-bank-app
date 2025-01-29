package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.model.Transaction;
import com.dlvb.asyncbankapp.repository.AccountRepository;
import com.dlvb.asyncbankapp.repository.TransactionRepository;
import com.dlvb.asyncbankapp.service.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Имплементация сервиса {@link TransactionService}.
 * @author Matushkin Anton
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final TransactionRepository transactionRepository;

    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    @Override
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

    @Override
    @Transactional
    public void processMultipleTransactions(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            processTransaction(transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
        }
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Void> processMultipleTransactionsAsync(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            processTransaction(transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void safeLockTransfer(String fromAccountId, String toAccountId, double amount) {
        boolean acquiredLock1 = false;
        boolean acquiredLock2 = false;
        try {
            acquiredLock1 = lock1.tryLock(1, TimeUnit.SECONDS);
            acquiredLock2 = lock2.tryLock(1, TimeUnit.SECONDS);

            if (acquiredLock1 && acquiredLock2) {
                Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
                Account toAccount = accountRepository.findById(toAccountId).orElseThrow();

                if (fromAccount.getBalance() < amount) {
                    throw new IllegalArgumentException("Insufficient balance");
                }
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);

            } else {
                throw new IllegalStateException("Unable to acquire locks, potential deadlock avoided");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquiredLock1) {
                lock1.unlock();
            }
            if (acquiredLock2) {
                lock2.unlock();
            }
        }
    }

}
