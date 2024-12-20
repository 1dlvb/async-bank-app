package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.TransactionDTO;
import com.dlvb.asyncbankapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTests {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void processTransactionsProcessesMultipleTransactions() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .fromAccountId("account1")
                .toAccountId("account2")
                .amount(100.0)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/process-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{ \"fromAccountId\": \"account1\"," +
                                " \"toAccountId\": \"account2\"," +
                                " \"amount\": 100.0," +
                                " \"timestamp\": \"" + transactionDTO.getTimestamp() + "\" }]"))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).processMultipleTransactions(any());
    }

    @Test
    void processTransactionsAsyncProcessesMultipleTransactionsAsync() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .fromAccountId("account1")
                .toAccountId("account2")
                .amount(150.0)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/process-transactionsAsync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{ \"fromAccountId\": \"account1\"," +
                                " \"toAccountId\": \"account2\"," +
                                " \"amount\": 150.0," +
                                " \"timestamp\": \"" + transactionDTO.getTimestamp() + "\" }]"))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).processMultipleTransactionsAsync(any());
    }
}
