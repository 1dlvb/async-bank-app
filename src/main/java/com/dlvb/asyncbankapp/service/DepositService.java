package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DepositService {

    Deposit getDepositById(String id);

    Deposit createDeposit(CreateOrUpdateDepositDTO depositDTO);

    double calculateDepositBalanceFixedRateByDate(LocalDate date, Deposit deposit);

    double calculateDepositBalanceByDateAndRate(LocalDate date, double rate, Deposit deposit);

    double calculateUpdatableDepositBalanceByDate(LocalDate date, double operation, Deposit deposit);

    double calculateUpdatableDepositBalanceByDate(LocalDate date, double fixedTopUps, double fixedWithdraw, Deposit deposit);

    Map<String, String> getCalculationsByDateAndRate(LocalDate date, double rate, String depositId);

    Map<String, String> getCalculationsByDateAndRateAsync(LocalDate date, double rate, String depositId);

    Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccounts(LocalDate date, double rate, List<String> depositIds);

    Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccountsAsync(LocalDate date, double rate, List<String> depositIds);

}
