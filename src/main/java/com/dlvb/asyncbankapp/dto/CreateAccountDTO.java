package com.dlvb.asyncbankapp.dto;

import com.dlvb.asyncbankapp.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {

    private String owner;
    private double balance;

    public static Account fromDTO(CreateAccountDTO dto) {
        return Account.builder()
                .owner(dto.getOwner())
                .balance(dto.getBalance())
                .build();
    }

    public static CreateAccountDTO toDTO(Account account) {
        return CreateAccountDTO.builder()
                .owner(account.getOwner())
                .balance(account.getBalance())
                .build();
    }

}
