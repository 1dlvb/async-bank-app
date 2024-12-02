package com.dlvb.asyncbankapp.dto;

import com.dlvb.asyncbankapp.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO класс для создания {@link Account}
 * @author Matushkin Anton
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {

    @Schema(description = "Наименование аккаунта", example = "Test LLC")
    private String owner;

    @Schema(description = "Начальный баланс", example = "50000")
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
