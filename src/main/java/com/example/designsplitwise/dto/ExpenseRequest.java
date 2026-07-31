package com.example.designsplitwise.dto;

import com.example.designsplitwise.model.SplitType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpenseRequest {
    private String name;
    private String description;
    private Double amount;
    private SplitType splitType;
    private String createdById;
    private ExpenseSplits splits;
}
