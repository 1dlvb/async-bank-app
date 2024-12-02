package com.dlvb.asyncbankapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO класс для передачи списка {@link com.dlvb.asyncbankapp.model.Account}
 * @author Matushkin Anton
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListOfAccountIdsDTO {

    @Schema(description = "Id аккаунтов", example = "[7a2a8a54-7852-439e-a6eb-2f8378820273," +
            " 7a2a8a54-7852-439e-a6eb-2f8378820274]")
    private List<String> accountIds;

}
