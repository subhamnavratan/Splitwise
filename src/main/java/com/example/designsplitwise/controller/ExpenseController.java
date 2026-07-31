package com.example.designsplitwise.controller;

import com.example.designsplitwise.dto.ExpenseBalanceDetail;
import com.example.designsplitwise.dto.ExpenseRequest;
import com.example.designsplitwise.dto.ExpenseResponse;
import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.service.ExpenseService;
import com.example.designsplitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/splitwise")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/create/expense")
    public ResponseEntity<?> createExpense(@RequestBody ExpenseRequest request) {
        String expenseId = expenseService.createExpense(request);
        return new ResponseEntity<>(expenseId, HttpStatus.CREATED);
    }

    @PostMapping("/create/expense/{groupId}")
    public ResponseEntity<?> createExpense(@PathVariable String groupId, @RequestBody ExpenseRequest request) {
        String expenseId = expenseService.createGroupExpense(request, groupId);
        return new ResponseEntity<>(expenseId, HttpStatus.CREATED);
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<?> getExpense(@PathVariable String expenseId) {
        ExpenseResponse response = expenseService.getExpenseSplitById(expenseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expense/user/{userId}")
    public ResponseEntity<?> getExpenseByUserId(@PathVariable String userId) {
        List<ExpenseBalanceDetail> detailList = expenseService.getExpenseBalanceDetailForUser(userId);
        return ResponseEntity.ok(detailList);
    }
}
