package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Transaction контроллер", description = "Контроллер для работы с транзакциями")
public class TransactionController {

    @NonNull
    private final TransactionService transactionService;

    @Operation(summary = "Последовательная обработка нескольких транзакций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции успешно обработаны")
    })
    @PostMapping("/process-transactions")
    public ResponseEntity<Void> processTransactions(@RequestBody List<TransactionDTO> transactions) {
        long startTime = System.currentTimeMillis();

        transactionService.processMultipleTransactions(transactions);

        long endTime = System.currentTimeMillis();
        log.info("Sequential transaction processing completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Асинхронная обработка нескольких транзакций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции успешно обработаны асинхронно")
    })
    @PostMapping("/process-transactionsAsync")
    public ResponseEntity<Void> processTransactionsAsync(@RequestBody List<TransactionDTO> transactions) {
        long startTime = System.currentTimeMillis();

        transactionService.processMultipleTransactionsAsync(transactions);

        long endTime = System.currentTimeMillis();
        log.info("Asynchronous transaction processing completed in " + (endTime - startTime) + " ms");
        return ResponseEntity.ok().build();
    }

}
