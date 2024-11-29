package com.dlvb.asyncbankapp.dto;

import com.dlvb.asyncbankapp.model.Deposit;
import com.dlvb.asyncbankapp.service.AccountService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateDepositDTO {

    private double balance;

    private double rate;

    @JsonProperty("account_id")
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
