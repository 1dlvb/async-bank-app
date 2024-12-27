package com.dlvb.asyncbankapp.controller;

import com.dlvb.asyncbankapp.dto.CreateOrUpdateDepositDTO;
import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.DepositService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DepositControllerTests {

    private MockMvc mockMvc;

    @Mock
    private DepositService depositService;

    @InjectMocks
    private DepositController depositController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(depositController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testOpenDepositCreatesDeposit() throws Exception {
        UUID uuid = UUID.randomUUID();
        CreateOrUpdateDepositDTO createOrUpdateDepositDTO = CreateOrUpdateDepositDTO.builder()
                .accountId(String.valueOf(uuid))
                .balance(100)
                .rate(5)
                .build();

        when(depositService.createDeposit(createOrUpdateDepositDTO)).thenReturn(new Deposit());

        mockMvc.perform(post("/api/open-deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrUpdateDepositDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(depositService, times(1)).createDeposit(createOrUpdateDepositDTO);
    }

    @Test
    void testGetStatisticsReturnsCalculations() throws Exception {
        String depositId = "test-id";
        double rate = 5.0;
        int year = 2025;

        Map<String, String> calculations = Map.of("key", "value");

        when(depositService.getCalculationsByDateAndRate(LocalDate.of(year, 1, 1), rate, depositId))
                .thenReturn(calculations);

        mockMvc.perform(get("/api/get-statistics")
                        .param("rate", String.valueOf(rate))
                        .param("depositId", depositId)
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(calculations)));

        verify(depositService, times(1))
                .getCalculationsByDateAndRate(LocalDate.of(year, 1, 1), rate, depositId);
    }

    @Test
    void testGetStatisticsForMultipleAccountsReturnsCalculations() throws Exception {
        List<String> depositIds = List.of("deposit-id-1", "deposit-id-2");
        int year = 2025;

        Map<String, Map<String, String>> calculations = Map.of(
                "deposit-id-1", Map.of("key1", "value1"),
                "deposit-id-2", Map.of("key2", "value2")
        );

        when(depositService.getCalculationsByDateAndRateForMultipleAccounts(LocalDate.of(year, 1, 1), 10, depositIds))
                .thenReturn(calculations);

        mockMvc.perform(post("/api/get-statistics-for-multiple-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositIds))
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(calculations)));

        verify(depositService, times(1))
                .getCalculationsByDateAndRateForMultipleAccounts(LocalDate.of(year, 1, 1), 10, depositIds);
    }

    @Test
    void testGetStatisticsForMultipleAccountsAsyncReturnsCalculations() throws Exception {
        List<String> depositIds = List.of("deposit-id-1", "deposit-id-2");
        int year = 2025;

        Map<String, Map<String, String>> calculations = Map.of(
                "deposit-id-1", Map.of("key1", "value1"),
                "deposit-id-2", Map.of("key2", "value2")
        );

        when(depositService.getCalculationsByDateAndRateForMultipleAccountsAsync(LocalDate.of(year, 1, 1), 10, depositIds))
                .thenReturn(calculations);

        mockMvc.perform(post("/api/get-statistics-for-multiple-accounts-async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositIds))
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(calculations)));

        verify(depositService, times(1))
                .getCalculationsByDateAndRateForMultipleAccountsAsync(LocalDate.of(year, 1, 1), 10, depositIds);
    }

    @Test
    void testCalculateVolatilityReturnsProperVolatility() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 100;
        double expectedVolatility = 5.5;

        when(depositService.calculateVolatility(currentTime, iterations)).thenReturn(expectedVolatility);

        mockMvc.perform(get("/api/calculate-volatility")
                        .param("currentTime", String.valueOf(currentTime))
                        .param("iterations", String.valueOf(iterations)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedVolatility)));

        verify(depositService, times(1)).calculateVolatility(currentTime, iterations);
    }
    @Test
    void testCalculateVolatilityWhenExecutionException() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 100;

        when(depositService.calculateVolatility(currentTime, iterations))
                .thenThrow(new ExecutionException("Execution failed", new RuntimeException()));

        mockMvc.perform(get("/api/calculate-volatility")
                        .param("currentTime", String.valueOf(currentTime))
                        .param("iterations", String.valueOf(iterations)))
                .andExpect(status().isInternalServerError());

        verify(depositService, times(1)).calculateVolatility(currentTime, iterations);
    }


    @Test
    void testCalculateVolatilityRunnableReturnsProperVolatility() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 100;
        double expectedVolatility = 10.5;

        when(depositService.calculateVolatilityWithRunnable(currentTime, iterations)).thenReturn(expectedVolatility);

        mockMvc.perform(get("/api/calculate-volatility-runnable")
                        .param("currentTime", String.valueOf(currentTime))
                        .param("iterations", String.valueOf(iterations)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedVolatility)));

        verify(depositService, times(1)).calculateVolatilityWithRunnable(currentTime, iterations);
    }

    @Test
    void testCalculateVolatilityFutureWhenExecutionException() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 100;

        when(depositService.calculateVolatilityFuture(currentTime, iterations))
                .thenThrow(new ExecutionException("Execution failed", new RuntimeException()));

        mockMvc.perform(get("/api/calculate-volatility-future")
                        .param("currentTime", String.valueOf(currentTime))
                        .param("iterations", String.valueOf(iterations)))
                .andExpect(status().isInternalServerError());

        verify(depositService, times(1)).calculateVolatilityFuture(currentTime, iterations);
    }


    @Test
    void testCalculateVolatilityFutureReturnsProperVolatility() throws Exception {
        long currentTime = System.currentTimeMillis();
        int iterations = 100;
        double expectedVolatility = 10.5;

        when(depositService.calculateVolatilityFuture(currentTime, iterations)).thenReturn(expectedVolatility);

        mockMvc.perform(get("/api/calculate-volatility-future")
                        .param("currentTime", String.valueOf(currentTime))
                        .param("iterations", String.valueOf(iterations)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedVolatility)));

        verify(depositService, times(1)).calculateVolatilityFuture(currentTime, iterations);
    }

}