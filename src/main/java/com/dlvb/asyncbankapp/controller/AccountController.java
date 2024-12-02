package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Account контроллер", description = "Контроллер для работы с аккаунтами")
public class AccountController {

    @NonNull
    private final AccountService accountService;

    @Operation(summary = "Создать новый аккаунт", description = "Создаёт новый аккаунт на основе предоставленных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное создание аккаунта",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))),
    })
    @PostMapping("/create-account")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountDTO createAccountRequest) {
        return ResponseEntity.ok(accountService.createAccount(createAccountRequest));
    }

    @Operation(summary = "Обновить балансы аккаунтов", description = "Последовательно обновляет балансы аккаунтов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Балансы успешно обновлены"),
    })
    @PostMapping("/update-balances")
    public ResponseEntity<Void> updateBalances(@RequestBody List<BalanceUpdateRequest> requests) {
        long startTime = System.currentTimeMillis();

        accountService.updateMultipleBalances(requests);

        long endTime = System.currentTimeMillis();
        log.info("Sequential balance update completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Асинхронное обновление балансов аккаунтов", description = "Асинхронно обновляет балансы аккаунтов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Балансы успешно обновлены асинхронно"),
    })
    @PostMapping("/update-balances-async")
    public ResponseEntity<Void> updateBalancesAsync(@RequestBody List<BalanceUpdateRequest> requests) {
        long startTime = System.currentTimeMillis();

        accountService.updateMultipleBalancesAsync(requests);

        long endTime = System.currentTimeMillis();
        log.info("Asynchronous balance update completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

}
