package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.model.Transaction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для обработки транзакций.
 * @author Matushkin Anton
 */
public interface TransactionService {

    /**
     * Производит транзакцию между двумя аккаунтами.
     *
     * @param fromAccountId идентификатор аккаунта отправителя.
     * @param toAccountId идентификатор аккаунта получателя.
     * @param amount сумма транзакции.
     * @return CompletableFuture, который возвращает транзакцию после её обработки.
     */
    CompletableFuture<Transaction> processTransaction(String fromAccountId, String toAccountId, double amount);

    /**
     * Обрабатывает несколько транзакций синхронно.
     *
     * @param transactions список транзакций для обработки.
     */
    void processMultipleTransactions(List<TransactionDTO> transactions);

    /**
     * Обрабатывает несколько транзакций асинхронно.
     *
     * @param transactions список транзакций для обработки.
     * @return CompletableFuture, который сигнализирует о завершении обработки транзакций.
     */
    CompletableFuture<Void> processMultipleTransactionsAsync(List<TransactionDTO> transactions);


    /**
     * Обрабатывает перевод в безопасном режиме. С локами.
     *
     * @param fromAccountId идентификатор аккаунта отправителя.
     * @param toAccountId идентификатор аккаунта получателя.
     * @param amount сумма транзакции.
     */
    void safeLockTransfer(String fromAccountId, String toAccountId, double amount);

}
