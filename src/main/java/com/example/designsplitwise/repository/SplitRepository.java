package com.example.designsplitwise.repository;

import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.model.Split;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SplitRepository extends CrudRepository<Split, String>  {
    List<Split> findByExpenseId(String expenseId);
    Optional<Split> findByExpenseIdAndUserId(String expenseId, String userId);
    List<Split> findByExpenseIn(List<Expense> expenses);
}
