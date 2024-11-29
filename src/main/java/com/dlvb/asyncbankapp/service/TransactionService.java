package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.model.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {

    CompletableFuture<Transaction> processTransaction(String fromAccountId, String toAccountId, double amount);

    void processMultipleTransactions(List<TransactionDTO> transactions);

    CompletableFuture<Void> processMultipleTransactionsAsync(List<TransactionDTO> transactions);

}
