package com.example.designsplitwise.repository;

import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, String> {
    Expense getExpenseById(String id);
    List<Expense> getAllByCreatedBy(User createdBy);
}
