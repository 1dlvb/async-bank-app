package com.dlvb.asyncbankapp.dto;

import com.dlvb.asyncbankapp.model.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO класс для обновления {@link Account#getBalance()}
 * @author Matushkin Anton
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest {

    @JsonProperty("account_id")
    @Schema(description = "Id аккаунта", example = "7a2a8a54-7852-439e-a6eb-2f8378820273")
    private String accountId;

    @Schema(description = "Количество денег для обновления", example = "200")
    private double amount;

}
