package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.BalanceUpdateRequest;
import com.dlvb.asyncbankapp.dto.CreateAccountDTO;
import com.dlvb.asyncbankapp.model.Account;
import com.dlvb.asyncbankapp.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTests {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateAccountSuccess() throws Exception {
        CreateAccountDTO accountDTO = CreateAccountDTO.builder()
                .balance(100.0)
                .owner("testOwner")
                .build();

        UUID uuid = UUID.randomUUID();
        Account createdAccount = new Account();
        createdAccount.setId(String.valueOf(uuid));
        createdAccount.setBalance(100);
        createdAccount.setOwner("testOwner");

        when(accountService.createAccount(accountDTO)).thenReturn(createdAccount);

        String accountJson = objectMapper.writeValueAsString(accountDTO);

        mockMvc.perform(post("/api/create-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(createdAccount)));
        verify(accountService, times(1)).createAccount(accountDTO);
    }

    @Test
    void testUpdateBalancesUpdatesBalancesSuccess() throws Exception {
        UUID uuid = UUID.randomUUID();

        BalanceUpdateRequest request = BalanceUpdateRequest.builder()
                .accountId(String.valueOf(uuid))
                .amount(100)
                .build();

        doNothing().when(accountService).updateMultipleBalances(List.of(request));

        mockMvc.perform(post("/api/update-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isOk());

        verify(accountService, times(1)).updateMultipleBalances(List.of(request));
    }

    @Test
    void testUpdateBalancesUpdatesBalancesAsyncSuccess() throws Exception {
        UUID uuid = UUID.randomUUID();

        BalanceUpdateRequest request = BalanceUpdateRequest.builder()
                .accountId(String.valueOf(uuid))
                .amount(100)
                .build();

        CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);
        when(accountService.updateMultipleBalancesAsync(List.of(request))).thenReturn(completableFuture);

        mockMvc.perform(post("/api/update-balances-async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isOk());

        verify(accountService, times(1)).updateMultipleBalancesAsync(List.of(request));
    }

}
