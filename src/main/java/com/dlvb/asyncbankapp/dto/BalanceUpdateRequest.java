package com.dlvb.asyncbankapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest {

    @JsonProperty("account_id")
    private String accountId;

    private double amount;

}
