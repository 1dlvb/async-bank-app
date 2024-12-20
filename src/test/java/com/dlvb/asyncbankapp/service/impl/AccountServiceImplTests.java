package com.dlvb.asyncbankapp.service.impl;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class AccountServiceImplTests {

    @Autowired
    private AccountService accountService;

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
    void testCreateAccountCreatesAccount() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        Account foundAccount = accountService.findById(account.getId());
        assertNotNull(foundAccount);
        assertEquals("test", foundAccount.getOwner());

    }

    @Test
    void testUpdateBalanceUpdatesBalanceWhenAccountIsPresent() {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());

        Account updatedAccount = accountService.updateBalance(account.getId(), 100);
        assertEquals(200, updatedAccount.getBalance());

    }

    @Test
    void testUpdateBalanceUpdatesBalanceWhenAccountIsNotPresent() {
        Account account = accountService.updateBalance(String.valueOf(UUID.randomUUID()), 100);
        assertNull(account);

    }

    @Test
    void testUpdateBalanceAsyncUpdatesBalanceWhenAccountIsPresent() throws ExecutionException, InterruptedException {
        Account account = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test")
                .balance(100)
                .build());
        CompletableFuture<Account> future = accountService.updateBalanceAsync(account.getId(), 100);
        Account updateBalanceAsync = future.get();
        assertEquals(200, updateBalanceAsync.getBalance());

    }

    @Test
    void testUpdateBalanceAsyncUpdatesBalanceWhenAccountIsNotPresent() throws ExecutionException, InterruptedException {
        CompletableFuture<Account> future = accountService.updateBalanceAsync(String.valueOf(UUID.randomUUID()), 100);
        Account updatedBalanceAsync = future.get();
        assertNull(updatedBalanceAsync);

    }

    @Test
    void testUpdateMultipleBalancesSetsProperValues() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(100)
                .build());

        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(100)
                .build());

        accountService.updateMultipleBalances(List.of(
                BalanceUpdateRequest.builder().accountId(account1.getId()).amount(101).build(),
                BalanceUpdateRequest.builder().accountId(account2.getId()).amount(102).build()));

        assertEquals(201, accountService.findById(account1.getId()).getBalance());
        assertEquals(202, accountService.findById(account2.getId()).getBalance());

    }

    @Test
    void testUpdateMultipleBalancesAsyncSetsProperValues() {
        Account account1 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(100)
                .build());

        Account account2 = accountService.createAccount(CreateAccountDTO.builder()
                .owner("test1")
                .balance(100)
                .build());

        accountService.updateMultipleBalancesAsync(List.of(
                BalanceUpdateRequest.builder().accountId(account1.getId()).amount(101).build(),
                BalanceUpdateRequest.builder().accountId(account2.getId()).amount(102).build()));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
        assertEquals(201, accountService.findById(account1.getId()).getBalance()));
        assertEquals(202, accountService.findById(account2.getId()).getBalance());
    }

}