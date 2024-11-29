package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.service.AccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    @NonNull
    private final AccountService accountService;

    @PostMapping("/create-account")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountDTO createAccountRequest) {
        return ResponseEntity.ok(accountService.createAccount(createAccountRequest));
    }

    @PostMapping("/update-balances")
    public ResponseEntity<Void> updateBalances(@RequestBody List<BalanceUpdateRequest> requests) {
        long startTime = System.currentTimeMillis();

        accountService.updateMultipleBalances(requests);

        long endTime = System.currentTimeMillis();
        log.info("Sequential balance update completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-balances-async")
    public ResponseEntity<Void> updateBalancesAsync(@RequestBody List<BalanceUpdateRequest> requests) {
        long startTime = System.currentTimeMillis();

        accountService.updateMultipleBalancesAsync(requests);

        long endTime = System.currentTimeMillis();
        log.info("Asynchronous balance update completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

}
