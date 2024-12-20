package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для управления аккаунтами и балансами пользователей.
 * @author Matushkin Anton
 */
public interface AccountService {

    /**
     * Находит аккаунт по его идентификатору.
     *
     * @param accountId идентификатор аккаунта.
     * @return аккаунт с указанным идентификатором, или {@code null}, если аккаунт не найден.
     */
    Account findById(String accountId);

    /**
     * Создает новый аккаунт.
     *
     * @param accountDTO dto, содержащий данные для создания аккаунта.
     * @return созданный аккаунт.
     */
    Account createAccount(CreateAccountDTO accountDTO);

    /**
     * Обновляет баланс указанного аккаунта на заданную сумму.
     *
     * @param accountId идентификатор аккаунта.
     * @param amount сумма, на которую необходимо обновить баланс.
     * @return обновленный аккаунт.
     */
    Account updateBalance(String accountId, double amount);

    /**
     * Асинхронно обновляет баланс указанного аккаунта на заданную сумму.
     *
     * @param accountId идентификатор аккаунта.
     * @param amount сумма, на которую необходимо обновить баланс.
     * @return {@link CompletableFuture}, который будет содержать обновленный аккаунт.
     */

    CompletableFuture<Account> updateBalanceAsync(String accountId, double amount);

    /**
     * Обновляет балансы для нескольких аккаунтов.
     *
     * @param requests список DTO для обновления баланса.
     */
    void updateMultipleBalances(List<BalanceUpdateRequest> requests);

    /**
     * Асинхронно обновляет балансы для нескольких аккаунтов.
     *
     * @param requests список DTO для обновления баланса.
     * @return {@link CompletableFuture}, который завершится после обработки всех запросов.
     */
    CompletableFuture<Void> updateMultipleBalancesAsync(List<BalanceUpdateRequest> requests);

}
