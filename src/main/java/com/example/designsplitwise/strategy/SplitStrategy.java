package com.example.designsplitwise.strategy;

import com.example.designsplitwise.dto.ExpenseSplits;
import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.model.Split;
import com.example.designsplitwise.model.User;

import java.util.List;

public interface SplitStrategy {
    List<Split> calculateSplits(Expense expense, ExpenseSplits splits);
}
