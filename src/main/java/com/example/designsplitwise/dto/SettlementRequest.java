package com.example.designsplitwise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementRequest {
    private String paidByUserId;
    private String paidToUserId;
    private String expenseId;
    private Double amount;
}
