package com.dlvb.asyncbankapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO класс для произведения транзакции {@link com.dlvb.asyncbankapp.model.Transaction}
 * @author Matushkin Anton
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @Schema(description = "Id аккаунта отправки", example = "7a2a8a54-7852-439e-a6eb-2f8378820273")
    private String fromAccountId;

    @Schema(description = "Id аккаунта получения", example = "7a2a8a54-7852-439e-a6eb-2f8378820274")
    private String toAccountId;

    @Schema(description = "Количество денег", example = "500")
    private double amount;

    @Schema(description = "Время отправки", example = "2024-12-01T14:30")
    private LocalDateTime timestamp;

}
