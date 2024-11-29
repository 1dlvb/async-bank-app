package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AccountService {

    Account createAccount(CreateAccountDTO accountDTO);

    Account updateBalance(String accountId, double amount);

    CompletableFuture<Account> updateBalanceAsync(String accountId, double amount);

    void updateMultipleBalances(List<BalanceUpdateRequest> requests);

    CompletableFuture<Void> updateMultipleBalancesAsync(List<BalanceUpdateRequest> requests);

    Account findById(String accountId);

}
