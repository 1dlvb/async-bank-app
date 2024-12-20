package com.dlvb.asyncbankapp.dto;

import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.AccountService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO класс для создания {@link Deposit}
 * @author Matushkin Anton
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateDepositDTO {

    @Schema(description = "Начальный баланс", example = "50000")
    private double balance;

    @Schema(description = "Процентная ставка", example = "5")
    private double rate;

    @JsonProperty("account_id")
    @Schema(description = "Id аккаунта", example = "7a2a8a54-7852-439e-a6eb-2f8378820273")
    private String accountId;

    public static Deposit fromDTO(CreateOrUpdateDepositDTO dto, AccountService accountService) {
        return Deposit.builder()
                .account(accountService.findById(dto.getAccountId()))
                .rate(dto.getRate())
                .balance(dto.getBalance())
                .build();
    }

    public static CreateOrUpdateDepositDTO fromDTO(Deposit deposit) {
        return CreateOrUpdateDepositDTO.builder()
                .accountId(deposit.getAccount().getId())
                .rate(deposit.getRate())
                .balance(deposit.getBalance())
                .build();
    }

}
