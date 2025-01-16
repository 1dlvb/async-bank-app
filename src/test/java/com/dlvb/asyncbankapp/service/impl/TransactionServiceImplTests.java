package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.service.AccountService;
import com.dlvb.asyncbankapp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
@Testcontainers
class TransactionServiceImplTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Test
    void testProcessTransactionUpdatesTwoAccountsBalances() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        transactionService.processTransaction(account1.getId(), account2.getId(), 100);
        account1 = accountService.findById(account1.getId());
        account2 = accountService.findById(account2.getId());

        assertEquals(0, account1.getBalance());
        assertEquals(200, account2.getBalance());
    }

    @Test
    void testProcessMultipleTransactionsUpdatesAccountBalances() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account3 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account4 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        transactionService.processMultipleTransactions(
                List.of(
                        TransactionDTO.builder().fromAccountId(account1.getId()).toAccountId(account2.getId()).amount(100).build(),
                        TransactionDTO.builder().fromAccountId(account3.getId()).toAccountId(account4.getId()).amount(100).build()
                        ));

        account1 = accountService.findById(account1.getId());
        account2 = accountService.findById(account2.getId());
        account3 = accountService.findById(account3.getId());
        account4 = accountService.findById(account4.getId());

        assertEquals(0, account1.getBalance());
        assertEquals(200, account2.getBalance());
        assertEquals(0, account3.getBalance());
        assertEquals(200, account4.getBalance());
    }

    @Test
    void testProcessMultipleTransactionsAsyncUpdatesAccountBalances() {

        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account3 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account4 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        transactionService.processMultipleTransactionsAsync(
                List.of(
                        TransactionDTO.builder().fromAccountId(account1.getId()).toAccountId(account2.getId()).amount(100).build(),
                        TransactionDTO.builder().fromAccountId(account3.getId()).toAccountId(account4.getId()).amount(100).build()
                ));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Account updatedAccount1 = accountService.findById(account1.getId());
            Account updateAccount2 = accountService.findById(account2.getId());assertNotNull(updatedAccount1);
            Account updateAccount3 = accountService.findById(account3.getId());
            Account updateAccount4 = accountService.findById(account4.getId());

            assertEquals(0, updatedAccount1.getBalance());
            assertEquals(200, updateAccount2.getBalance());
            assertEquals(0, updateAccount3.getBalance());
            assertEquals(200, updateAccount4.getBalance());
        });

    }

    @Test
    void testSafeLockTransfer_shouldNotFallWithDeadlock() throws Exception {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> transactionService.safeLockTransfer(account1.getId(), account2.getId(), 100));
        Future<?> future2 = executor.submit(() -> transactionService. safeLockTransfer(account2.getId(), account1.getId(), 100));

        future1.get(5, TimeUnit.SECONDS);
        future2.get(5, TimeUnit.SECONDS);

        boolean deadlockDetected = detectDeadlock();
        assertFalse(deadlockDetected);
    }

    @Test
    void testSafeLockTransfer_noRaceConditions() throws InterruptedException {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(1000)
                .build());
        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(1000)
                .build());

        double initialFromAccountBalance = account1.getBalance();
        double initialToAccountBalance = account2.getBalance();

        int threadCount = 100;
        double amount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                transactionService.safeLockTransfer(account1.getId(), account2.getId(), amount);
                return null;
            });
        }

        executorService.invokeAll(tasks);
        executorService.shutdown();

        Account finalFromAccount = accountService.findById(account1.getId());
        Account finalToAccount = accountService.findById(account2.getId());

        assertEquals(initialFromAccountBalance - (amount * threadCount), finalFromAccount.getBalance());
        assertEquals(initialToAccountBalance + (amount * threadCount), finalToAccount.getBalance());
    }

    private boolean detectDeadlock() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            for (long threadId : deadlockedThreads) {
                Thread thread = getThread(threadId);
                if (thread != null) {
                    System.err.println("Deadlock detected in thread: " + thread.getName());
                }
            }
            return true;
        }
        return false;
    }

    private Thread getThread(long threadId) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.threadId() == threadId) {
                return thread;
            }
        }
        return null;
    }

}