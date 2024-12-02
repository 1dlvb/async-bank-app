package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.repository.AccountRepository;
import com.dlvb.asyncbankapp.service.AccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Имплементация сервиса {@link AccountService}.
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @NonNull
    private final AccountRepository accountRepository;

    @Override
    public Account findById(String accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }

    @Override
    public Account createAccount(CreateAccountDTO accountDTO) {
        return accountRepository.save(CreateAccountDTO.fromDTO(accountDTO));
    }

    @Override
    @Transactional
    public Account updateBalance(String accountId, double amount) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() + amount);
            return accountRepository.save(optionalAccount.get());
        }
        return null;
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Account> updateBalanceAsync(String accountId, double amount) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() + amount);
            return CompletableFuture.completedFuture(accountRepository.save(optionalAccount.get()));
        }
        return null;
    }

    @Override
    @Transactional
    public void updateMultipleBalances(List<BalanceUpdateRequest> requests) {
        for (BalanceUpdateRequest request : requests) {
            updateBalance(request.getAccountId(), request.getAmount());
        }
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Void> updateMultipleBalancesAsync(List<BalanceUpdateRequest> requests) {
        for (BalanceUpdateRequest request : requests) {
            updateBalanceAsync(request.getAccountId(), request.getAmount());
        }
        return CompletableFuture.completedFuture(null);
    }

}
