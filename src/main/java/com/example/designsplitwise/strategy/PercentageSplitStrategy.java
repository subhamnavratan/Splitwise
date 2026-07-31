package com.example.designsplitwise.strategy;

import com.example.designsplitwise.dto.ExpenseSplits;
import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.model.Split;
import com.example.designsplitwise.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(Expense expense, ExpenseSplits expenseSplits){
        List<Split> splits = new ArrayList<>();
        Map<String, Double> percentages = expenseSplits.getPercentages();

        for(User user : expenseSplits.getUsers()) {
            Split split = new Split();
            split.setUser(user);
            split.setExpense(expense);

            Double percentage = percentages.getOrDefault(user.getId(), 0.0);
            Double amount = expense.getAmount() * (percentage / 100.0);

            split.setSplitAmount(amount);
            split.setOwe(!user.getId().equals(expense.getCreatedBy().getId()));
            splits.add(split);
        }

        return splits;
    }
}