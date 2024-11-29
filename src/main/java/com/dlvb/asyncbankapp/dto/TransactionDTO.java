package com.dlvb.asyncbankapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private String fromAccountId;
    private String toAccountId;
    private double amount;
    private LocalDateTime timestamp;

}
