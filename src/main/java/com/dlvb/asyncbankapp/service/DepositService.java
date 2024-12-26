package com.dlvb.asyncbankapp.service;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Сервис для управления вкладами.
 * @author Matushkin Anton
 */
public interface DepositService {

    /**
     * Получает депозит по его идентификатору.
     *
     * @param id идентификатор депозита.
     * @return депозит с указанным идентификатором.
     */
    Deposit getDepositById(String id);

    /**
     * Создает новый депозит на основе предоставленных данных.
     *
     * @param depositDTO объект, содержащий данные для создания депозита.
     * @return созданный депозит.
     */
    Deposit createDeposit(CreateOrUpdateDepositDTO depositDTO);

    /**
     * Рассчитывает баланс депозита с фиксированной ставкой на указанную дату.
     *
     * @param date дата, на которую необходимо рассчитать баланс.
     * @param deposit депозит, для которого выполняется расчет.
     * @return баланс депозита на указанную дату.
     */
    double calculateDepositBalanceFixedRateByDate(LocalDate date, Deposit deposit);

    /**
     * Рассчитывает баланс депозита на указанную дату с учетом указанной ставки.
     *
     * @param date дата, на которую необходимо рассчитать баланс.
     * @param rate ставка депозита.
     * @param deposit депозит, для которого выполняется расчет.
     * @return баланс депозита на указанную дату с учетом ставки.
     */
    double calculateDepositBalanceByDateAndRate(LocalDate date, double rate, Deposit deposit);

    /**
     * Рассчитывает баланс депозита на указанную дату с учетом операции (пополнение или снятие).
     *
     * @param date дата, на которую необходимо рассчитать баланс.
     * @param operation сумма операции (пополнение или снятие).
     * @param deposit депозит, для которого выполняется расчет.
     * @return обновленный баланс депозита с учетом операции.
     */
    double calculateUpdatableDepositBalanceByDate(LocalDate date, double operation, Deposit deposit);

    /**
     * Рассчитывает баланс депозита на указанную дату с учетом фиксированных пополнений и снятий.
     *
     * @param date дата, на которую необходимо рассчитать баланс.
     * @param fixedTopUps сумма фиксированных пополнений.
     * @param fixedWithdraw сумма фиксированных снятий.
     * @param deposit депозит, для которого выполняется расчет.
     * @return обновленный баланс депозита с учетом пополнений и снятий.
     */
    double calculateUpdatableDepositBalanceByDate(LocalDate date, double fixedTopUps, double fixedWithdraw, Deposit deposit);

    /**
     * Получает статистику для депозита на указанную дату.
     *
     * @param date дата, на которую необходимо получить расчеты.
     * @param rate ставка депозита.
     * @param depositId идентификатор депозита.
     * @return карта с расчетами для депозита.
     */
    Map<String, String> getCalculationsByDateAndRate(LocalDate date, double rate, String depositId);

    /**
     * Получает расчеты для нескольких депозитов на указанную дату.
     *
     * @param date дата, на которую необходимо получить расчеты.
     * @param rate ставка депозита.
     * @param depositIds список идентификаторов депозитов.
     * @return карта с расчетами для нескольких депозитов.
     */
    Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccounts(LocalDate date, double rate, List<String> depositIds);

    /**
     * Асинхронно получает расчеты для нескольких депозитов на указанную дату.
     *
     * @param date дата, на которую необходимо получить расчеты.
     * @param rate ставка депозита.
     * @param depositIds список идентификаторов депозитов.
     * @return карта с асинхронными расчетами для нескольких депозитов.
     */
    Map<String, Map<String, String>> getCalculationsByDateAndRateForMultipleAccountsAsync(LocalDate date, double rate, List<String> depositIds);

    /**
     * Рассчитывает волатильность на основе различных факторов.
     * @param currentTime текущее время в миллисекундах.
     * @return вычисленная волатильность.
     */
    double calculateVolatility(long currentTime, int iterations) throws ExecutionException;

    /**
     * Рассчитывает волатильность (в многопотоке) на основе различных факторов.
     * @param currentTime текущее время в миллисекундах.
     * @return вычисленная волатильность.
     */
    double calculateVolatilityMultithreading(long currentTime, int iterations);

}
