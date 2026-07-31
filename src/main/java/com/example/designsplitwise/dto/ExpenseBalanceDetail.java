package com.example.designsplitwise.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class ExpenseBalanceDetail {
    private String expenseId;
    private String role;
    private Map<String, Double> balances;
}

