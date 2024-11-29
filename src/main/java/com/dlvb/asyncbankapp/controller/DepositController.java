package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.DepositService;
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

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepositController {

    @NonNull
    private final DepositService depositService;

    @PostMapping("/open-deposit")
    public ResponseEntity<Deposit> openDeposit(@RequestBody CreateOrUpdateDepositDTO createOrUpdateDepositDTO) {
        return ResponseEntity.ok(depositService.createDeposit(createOrUpdateDepositDTO));
    }

    @GetMapping("/get-statistics")
    public Map<String, String> getStatistics(@RequestParam double rate, @RequestParam String depositId) {
        long startTime = System.currentTimeMillis();
        Map<String, String> calculations = depositService.getCalculationsByDateAndRate(LocalDate.of(2056, 1, 1),
                rate, depositId);
        long endTime = System.currentTimeMillis();
        log.info("Sequential getting statistics completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @GetMapping("/get-statistics-async")
    public Map<String, String> getStatisticsAsync(@RequestParam double rate, @RequestParam String depositId) {
        long startTime = System.currentTimeMillis();
        Map<String, String> calculations = depositService.getCalculationsByDateAndRateAsync(LocalDate.of(2056, 1, 1),
                rate, depositId);
        long endTime = System.currentTimeMillis();
        log.info("Sequential getting statistics completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @PostMapping("/get-statistics-for-multiple-accounts")
    public Map<String, Map<String, String>> getStatisticsForListOfUsers(@RequestBody List<String> depositIds) {

        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> calculations =
                depositService.getCalculationsByDateAndRateForMultipleAccounts(LocalDate.of(2056, 1, 1), 10, depositIds);
        long endTime = System.currentTimeMillis();
        log.info("Sequential getting statistics for multiple accounts completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

    @PostMapping("/get-statistics-for-multiple-accounts-async")
    public Map<String, Map<String, String>> getStatisticsForListOfUsersAsync(@RequestBody List<String> depositIds) {

        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> calculations =
                depositService.getCalculationsByDateAndRateForMultipleAccountsAsync(
                        LocalDate.of(2056, 1, 1), 10, depositIds);
        long endTime = System.currentTimeMillis();
        log.info("Asynchronous getting statistics for multiple accounts completed in " + (endTime - startTime) + " ms.");
        return calculations;
    }

}
