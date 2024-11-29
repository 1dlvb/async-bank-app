package com.dlvb.asyncbankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private String fromAccountId;
    private String toAccountId;
    private double amount;
    private LocalDateTime timestamp;

}
