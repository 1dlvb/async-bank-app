package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.repository.DepositRepository;
import com.dlvb.asyncbankapp.service.AccountService;
import com.dlvb.asyncbankapp.service.DepositService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private static final double[] OPERATION_PERCENTAGE = {0.05, 0.1, 0.15, 0.2};
    private static final double[] TOP_UP_PERCENTAGES = {0.05, 0.1, 0.15, 0.2};
    private static final double[] WITHDRAW_PERCENTAGES = {0.025, 0.05, 0.1, 0.15};


    @NonNull
    private final DepositRepository depositRepository;

    @NonNull
    private final AccountService accountService;

    @Override
    public Deposit getDepositById(String id) {
        return depositRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Deposit not found for ID: " + id)
        );
    }

    @Override
    public Deposit createDeposit(CreateOrUpdateDepositDTO depositDTO) {
        return depositRepository.save(CreateOrUpdateDepositDTO.fromDTO(depositDTO, accountService));
    }

    @Override
    public double calculateDepositBalanceFixedRateByDate(LocalDate date, Deposit deposit) {
        LocalDate dateNow = LocalDate.now();
        int years = date.getYear() - dateNow.getYear();
        return calculateSimpleCompoundInterest(deposit.getBalance(), deposit.getRate(), years);
    }

    @Override
    public double calculateDepositBalanceByDateAndRate(LocalDate date, double rate, Deposit deposit) {
        LocalDate dateNow = LocalDate.now();
        int years = date.getYear() - dateNow.getYear();
        return calculateSimpleCompoundInterest(deposit.getBalance(), rate, years);
    }

    @Override
    public double calculateUpdatableDepositBalanceByDate(LocalDate date, double operation, Deposit deposit) {
        LocalDate dateNow = LocalDate.now();
        int years = date.getYear() - dateNow.getYear();
        return operation * calculateOperationRatio(deposit.getRate(), years)
                + calculateSimpleCompoundInterest(deposit.getBalance(), deposit.getRate(), years);
    }

    @Override
    public double calculateUpdatableDepositBalanceByDate(LocalDate date, double fixedTopUps,
                                                         double fixedWithdraw, Deposit deposit) {
        if (fixedWithdraw > fixedTopUps) {
            throw new IllegalArgumentException("Withdraw cannot be less than topUps.");
        }
        LocalDate dateNow = LocalDate.now();
        int years = date.getYear() - dateNow.getYear();
        double balance = deposit.getBalance();
        double rate = deposit.getRate();
        return calculateSimpleCompoundInterest(balance, rate, years) + (calculateOperationRatio(rate, years)
                * (fixedTopUps - fixedWithdraw));
    }

    @Override
    public Map<String, String> getCalculationsByDateAndRate(LocalDate date, double rate, String depositId) {

        Deposit deposit = getDepositById(depositId);
        double depositBalance = deposit.getBalance();

        Double balanceActualRate = calculateDepositBalanceFixedRateByDate(date, deposit);
        Double balanceByRate = calculateDepositBalanceByDateAndRate(date, rate, deposit);

        Map<String, Double> topUpBalances = calculateTopUpBalancesForStatistics(date, depositBalance, deposit);
        Map<String, Double> topUpAndWithdrawBalances = calculateTopUpAndWithdrawBalancesForStatistics(date, depositBalance, deposit);

        return formatCalculationsForStatistics(balanceByRate, balanceActualRate, topUpBalances, topUpAndWithdrawBalances);
    }

    @Override
    public Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccounts(LocalDate date, double rate, List<String> depositIds) {
        Map<String, Map<String, String>> calculationsForId = new HashMap<>();
        for (String depositId: depositIds) {
            calculationsForId.put(depositId, getCalculationsByDateAndRate(date, rate, depositId));
        }
        return calculationsForId;
    }

    @Override
    public Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccountsAsync(LocalDate date, double rate, List<String> depositIds) {
        Map<String, Map<String, String>> calculationsForId = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(depositIds.size());

        List<Callable<Map<String, String>>> tasks = depositIds.stream()
                .map(depositId -> (Callable<Map<String, String>>) ()
                        -> getCalculationsByDateAndRate(date, rate, depositId))
                .toList();

        try {
            List<Future<Map<String, String>>> futures = executorService.invokeAll(tasks);
            for (int i = 0; i < depositIds.size(); i++) {
                Future<Map<String, String>> future = futures.get(i);
                calculationsForId.put(depositIds.get(i), future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error occurred during calculations", e);
        } finally {
            executorService.shutdown();
        }

        return calculationsForId;
    }

    private Map<String, Double> calculateTopUpBalancesForStatistics(LocalDate date, double depositBalance, Deposit deposit) {
        Map<String, Double> balances = new LinkedHashMap<>();
        for (double percentage : OPERATION_PERCENTAGE) {
            double topUpAmount = depositBalance * percentage;
            balances.put(String.format("balance_with_%d_percents_top_ups", (int) (percentage * 100)),
                    calculateUpdatableDepositBalanceByDate(date, topUpAmount, deposit));
        }
        return balances;
    }

    private Map<String, Double> calculateTopUpAndWithdrawBalancesForStatistics(LocalDate date, double depositBalance, Deposit deposit) {
        Map<String, Double> balances = new LinkedHashMap<>();

        for (int i = 0; i < TOP_UP_PERCENTAGES.length; i++) {
            double topUpAmount = depositBalance * TOP_UP_PERCENTAGES[i];
            double withdrawAmount = -depositBalance * WITHDRAW_PERCENTAGES[i % WITHDRAW_PERCENTAGES.length];
            balances.put(
                    String.format("balance_with_%d_percents_top_ups_and_%d_percents_withdraw",
                            (int) (TOP_UP_PERCENTAGES[i] * 100),
                            (int) Math.abs(WITHDRAW_PERCENTAGES[i % WITHDRAW_PERCENTAGES.length] * 100)),
                    calculateUpdatableDepositBalanceByDate(date, topUpAmount, withdrawAmount, deposit)
            );
        }
        return balances;
    }

    private Map<String, String> formatCalculationsForStatistics(
            Double balanceByRate,
            Double balanceActualRate,
            Map<String, Double> topUpBalances,
            Map<String, Double> topUpAndWithdrawBalances) {

        Map<String, String> formattedCalculations = new LinkedHashMap<>();

        formattedCalculations.put("balance_by_rate", String.format("%.3f", balanceByRate));
        formattedCalculations.put("balance_with_actual_rate", String.format("%.3f", balanceActualRate));

        topUpBalances.forEach((key, value) -> formattedCalculations.put(key, String.format("%.3f", value)));
        topUpAndWithdrawBalances.forEach((key, value) -> formattedCalculations.put(key, String.format("%.3f", value)));

        return formattedCalculations;
    }

    private double calculateSimpleCompoundInterest(double initialPrincipalBalance,
                                                   double rateInPercents, double time) {
        return initialPrincipalBalance * Math.pow((1 + 0.01 * rateInPercents), time);
    }

    private double calculateOperationRatio(double rateInPercents, double time) {
        return (Math.pow(1 + 0.01 * rateInPercents, time) - 1) / (0.01 * rateInPercents);
    }

}
