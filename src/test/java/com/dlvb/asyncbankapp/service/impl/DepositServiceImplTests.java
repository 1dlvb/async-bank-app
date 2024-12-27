package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class DepositServiceImplTests {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private DepositServiceImpl depositService;

    @Autowired
    private AccountService accountService;

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void testCreateDepositReturnsAccount() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        CreateOrUpdateDepositDTO depositDTO = new CreateOrUpdateDepositDTO();
        depositDTO.setBalance(1000);
        depositDTO.setRate(5);
        depositDTO.setAccountId(account.getId());

        Deposit createdDeposit = depositService.createDeposit(depositDTO);

        assertNotNull(createdDeposit);
        assertEquals(1000, createdDeposit.getBalance());
        assertEquals(5, createdDeposit.getRate());
        assertEquals("test", createdDeposit.getAccount().getOwner());
    }

    @Test
    void testGetDepositByIdReturnsAccount() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        CreateOrUpdateDepositDTO depositDTO = new CreateOrUpdateDepositDTO();
        depositDTO.setBalance(1000);
        depositDTO.setRate(5);
        depositDTO.setAccountId(account.getId());

        Deposit deposit = depositService.createDeposit(depositDTO);


        Deposit retrievedDeposit = depositService.getDepositById(deposit.getId());

        assertNotNull(retrievedDeposit);
        assertEquals(1000, retrievedDeposit.getBalance());
        assertEquals(5, retrievedDeposit.getRate());
        assertEquals("test", retrievedDeposit.getAccount().getOwner());
    }

    @Test
    void testCalculateUpdatableDepositBalanceByDateCalculatesRight() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(1000)
                .build());

        CreateOrUpdateDepositDTO depositDTO = new CreateOrUpdateDepositDTO();
        depositDTO.setBalance(1000);
        depositDTO.setRate(5);
        depositDTO.setAccountId(account.getId());

        Deposit deposit = depositService.createDeposit(depositDTO);
        assertThrows(IllegalArgumentException.class, () -> depositService.calculateUpdatableDepositBalanceByDate(
                LocalDate.of(2056, 1, 1), 100, 200, deposit)) ;
    }

    @Test
    void testCalculateDepositBalanceFixedRateByDateCalculatesRight() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(1000)
                .build());

        CreateOrUpdateDepositDTO depositDTO = new CreateOrUpdateDepositDTO();
        depositDTO.setBalance(1000);
        depositDTO.setRate(5);
        depositDTO.setAccountId(account.getId());

        Deposit deposit = depositService.createDeposit(depositDTO);

        double calculatedBalance = depositService.calculateDepositBalanceFixedRateByDate(
                LocalDate.of(2056, 1, 1), deposit);

        assertEquals(String.format("%.3f", 4764.941), String.format("%.3f", calculatedBalance));
    }

    @Test
    void testGetCalculationsByDateAndRateForMultipleAccountsCalculatesRightForManyAccounts() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(1000)
                .build());

        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test2")
                .balance(1000)
                .build());

        CreateOrUpdateDepositDTO depositDTO1 = new CreateOrUpdateDepositDTO();
        depositDTO1.setBalance(1000);
        depositDTO1.setRate(5);
        depositDTO1.setAccountId(account1.getId());

        CreateOrUpdateDepositDTO depositDTO2 = new CreateOrUpdateDepositDTO();
        depositDTO2.setBalance(1000);
        depositDTO2.setRate(5);
        depositDTO2.setAccountId(account2.getId());

        Deposit deposit1 = depositService.createDeposit(depositDTO1);
        Deposit deposit2 = depositService.createDeposit(depositDTO2);

        List<String> depositIds = List.of(deposit1.getId(), deposit2.getId());
        Map<String, Map<String, String>> calculations = depositService.getCalculationsByDateAndRateForMultipleAccounts(
                LocalDate.of(2056, 1, 1), 3.0, depositIds);

        assertEquals(2, calculations.size());
    }

    @Test
    void testGetCalculationsByDateAndRateForMultipleAccountsAsyncCalculatesRightForManyAccounts() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(1000)
                .build());

        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test2")
                .balance(1000)
                .build());

        CreateOrUpdateDepositDTO depositDTO1 = new CreateOrUpdateDepositDTO();
        depositDTO1.setBalance(1000);
        depositDTO1.setRate(5);
        depositDTO1.setAccountId(account1.getId());

        CreateOrUpdateDepositDTO depositDTO2 = new CreateOrUpdateDepositDTO();
        depositDTO2.setBalance(1000);
        depositDTO2.setRate(5);
        depositDTO2.setAccountId(account2.getId());

        Deposit deposit1 = depositService.createDeposit(depositDTO1);
        Deposit deposit2 = depositService.createDeposit(depositDTO2);

        List<String> depositIds = List.of(deposit1.getId(), deposit2.getId());
        Map<String, Map<String, String>> calculations = depositService.getCalculationsByDateAndRateForMultipleAccountsAsync(
                LocalDate.of(2056, 1, 1), 3.0, depositIds);

        assertEquals(2, calculations.size());
    }

    @Test
    void testCalculateVolatilityReturnsCorrectValue() {
        assertTrue(depositService.calculateVolatility(System.currentTimeMillis(), 100) > 0);
    }

    @Test
    void testCalculateVolatilityHandlesZeroIterations() {
        assertEquals(0, depositService.calculateVolatility(System.currentTimeMillis(), 0));
    }

    @Test
    void testCalculateVolatilityWithRunnableReturnsCorrectValue() {
        assertTrue(depositService.calculateVolatilityWithRunnable(System.currentTimeMillis(), 100) > 0);
    }

    @Test
    void testCalculateVolatilityWithRunnableHandlesZeroIterations() {
        assertEquals(0, depositService.calculateVolatilityWithRunnable(System.currentTimeMillis(), 0));
    }

    @Test
    void testCalculateVolatilityFutureReturnsCorrectValue() throws Exception {
        assertTrue(depositService.calculateVolatilityFuture(System.currentTimeMillis(), 100) > 0);
    }

    @Test
    void testCalculateVolatilityFutureHandlesZeroIterations() throws Exception {
        assertEquals(0, depositService.calculateVolatilityFuture(System.currentTimeMillis(), 0));
    }

    @Test
    void testCalculateVolatilityAndRunnableAndParallelStreamResultsMatch() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 50;

        double singleThreadResult = depositService.calculateVolatility(currentTime, iterations);
        double multiThreadWithRunnableResult = depositService.calculateVolatilityWithRunnable(currentTime, iterations);
        double multiThreadFutureStreamResult = depositService.calculateVolatilityFuture(currentTime, iterations);

        assertEquals(singleThreadResult, multiThreadWithRunnableResult, 0.5);
        assertEquals(singleThreadResult, multiThreadFutureStreamResult, 0.5);
    }

}
