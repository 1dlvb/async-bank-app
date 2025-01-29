package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Deposit контроллер", description = "Контроллер для работы со вкладами")
public class DepositController {

    @NonNull
    private final DepositService depositService;

    @Operation(summary = "Открытие нового депозита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Депозит успешно открыт")
    })
    @PostMapping("/open-deposit")
    public ResponseEntity<Deposit> openDeposit(@RequestBody CreateOrUpdateDepositDTO createOrUpdateDepositDTO) {
        return ResponseEntity.ok(depositService.createDeposit(createOrUpdateDepositDTO));
    }

    @Operation(summary = "Получение статистики по депозиту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    })
    @GetMapping("/get-statistics")
    public Map<String, String> getStatistics(@RequestParam double rate, @RequestParam String depositId,
                                             @RequestParam int year) {
        long startTime = System.currentTimeMillis();
        Map<String, String> calculations = depositService.getCalculationsByDateAndRate(LocalDate.of(year, 1, 1),
                rate, depositId);
        long endTime = System.currentTimeMillis();
        log.info("Sequential getting statistics completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @Operation(summary = "Получение статистики по нескольким депозитам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика по нескольким депозитам успешно получена")
    })
    @PostMapping("/get-statistics-for-multiple-accounts")
    public Map<String, Map<String, String>> getStatisticsForListOfUsers(@RequestBody List<String> depositIds,
                                                                        @RequestParam int year) {

        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> calculations =
                depositService.getCalculationsByDateAndRateForMultipleAccounts(LocalDate.of(year, 1, 1),
                        10, depositIds);
        long endTime = System.currentTimeMillis();
        log.info("Sequential getting statistics for multiple accounts completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @Operation(summary = "Получение статистики по нескольким депозитам асинхронно")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика по нескольким депозитам успешно получена асинхронно")
    })
    @PostMapping("/get-statistics-for-multiple-accounts-async")
    public Map<String, Map<String, String>> getStatisticsForListOfUsersAsync(@RequestBody List<String> depositIds,
                                                                             @RequestParam int year) {

        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> calculations =
                depositService.getCalculationsByDateAndRateForMultipleAccountsAsync(
                        LocalDate.of(year, 1, 1), 10, depositIds);
        long endTime = System.currentTimeMillis();
        log.info("Asynchronous getting statistics for multiple accounts completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @Operation(summary = "Расчёт волатильности")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Волатильность успешно рассчитана")
    })
    @GetMapping("/calculate-volatility")
    public ResponseEntity<Double> calculateVolatility(@RequestParam long currentTime, @RequestParam int iterations) {

        try {
            long startTime = System.currentTimeMillis();

            double averageVolatility = depositService.calculateVolatility(currentTime, iterations);

            long endTime = System.currentTimeMillis();
            log.info("Calculating average volatility completed in " + (endTime - startTime) + " ms.");

            return ResponseEntity.ok(averageVolatility);

        } catch (Exception e) {
            log.error("Error calculating average volatility", e);
            return ResponseEntity.status(500).build();
        }

    }

    @Operation(summary = "Расчёт волатильности с использованием runnable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Волатильность успешно рассчитана с использованием runnable")
    })
    @GetMapping("/calculate-volatility-runnable")
    public ResponseEntity<Double> calculateVolatilityRunnable(@RequestParam long currentTime, @RequestParam int iterations) {
        long startTime = System.currentTimeMillis();

        double averageVolatility = depositService.calculateVolatilityWithRunnable(currentTime, iterations);

        long endTime = System.currentTimeMillis();
        log.info("Calculating average volatility(runnable) completed in " + (endTime - startTime) + " ms.");
        return ResponseEntity.ok(averageVolatility);
    }

    @Operation(summary = "Расчёт волатильности с использованием future")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Волатильность успешно рассчитана с использованием future")
    })
    @GetMapping("/calculate-volatility-future")
    public ResponseEntity<Double> calculateVolatilityFuture(@RequestParam long currentTime, @RequestParam int iterations) {

        try {
            long startTime = System.currentTimeMillis();

            double averageVolatility = depositService.calculateVolatilityFuture(currentTime, iterations);

            long endTime = System.currentTimeMillis();
            log.info("Calculating average volatility(future) completed in " + (endTime - startTime) + " ms.");
            return ResponseEntity.ok(averageVolatility);
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).build();
        }

    }

}
