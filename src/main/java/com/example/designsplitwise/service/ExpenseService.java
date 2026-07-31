package com.example.designsplitwise.service;

import com.example.designsplitwise.dto.*;
import com.example.designsplitwise.model.Expense;
import com.example.designsplitwise.model.Group;
import com.example.designsplitwise.model.Split;
import com.example.designsplitwise.model.User;
import com.example.designsplitwise.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final SplitService splitService;
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, SplitService splitService, GroupService groupService, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.splitService = splitService;
        this.groupService = groupService;
        this.userService = userService;
    }

    public String createExpense(ExpenseRequest request) {
        try{
            Expense expense = new Expense();
            expense.setAmount(request.getAmount());
            expense.setDescription(request.getDescription());
            expense.setName(request.getName());
            expense.setType(request.getSplitType());
            expense.setCreatedBy(userService.getUser(request.getCreatedById()));

            Expense savedExpense = expenseRepository.save(expense);

            ExpenseSplits expenseSplits = request.getSplits();
            List<User> users = userService.getUsers(expenseSplits.getUserIds());
            expenseSplits.setUsers(users);
            splitService.createSplits(savedExpense, expenseSplits);
            return savedExpense.getId();
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public String createGroupExpense(ExpenseRequest request, String groupId) {
        try{
            Expense expense = new Expense();
            expense.setAmount(request.getAmount());
            expense.setDescription(request.getDescription());
            expense.setName(request.getName());
            expense.setType(request.getSplitType());
            expense.setCreatedBy(userService.getUser(request.getCreatedById()));

            Group group = groupService.getGroup(groupId);
            expense.setGroup(group);
            Expense savedExpense = expenseRepository.save(expense);

            List<User> users = group.getGroupMembers();
            ExpenseSplits expenseSplits = new ExpenseSplits();
            expenseSplits.setUsers(users);

            splitService.createSplits(savedExpense, expenseSplits);
            return savedExpense.getId();
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public Expense getExpenseById(String id) {
        return expenseRepository.findById(id).get();
    }

    public ExpenseResponse getExpenseSplitById(String expenseId){
        Expense expense = expenseRepository.getExpenseById(expenseId);
        if (expense == null) {
            return null;
        }

        List<Split> splits = splitService.getSplitsByExpenseId(expenseId);
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setName(expense.getName());
        response.setDescription(expense.getDescription());
        response.setAmount(expense.getAmount());
        response.setSplitType(expense.getType());
        response.setCreatedBy(expense.getCreatedBy());
        response.setGroup(expense.getGroup());

        List<SplitResponse> splitResponses = new ArrayList<>();
        for (Split split : splits) {
            SplitResponse splitResponse = new SplitResponse();
            splitResponse.setId(split.getId());
            splitResponse.setUser(split.getUser());
            splitResponse.setAmount(split.getSplitAmount());
            splitResponse.setOwing(split.isOwe());
            splitResponse.setSettled(split.isSettled());
            splitResponse.setSettlementAmount(split.getSettledAmount());
            splitResponses.add(splitResponse);
        }

        response.setSplits(splitResponses);

        return response;
    }

    public List<ExpenseBalanceDetail> getExpenseBalanceDetailForUser(String userId){
        List<Expense> expenses = expenseRepository.getAllByCreatedBy(userService.getUser(userId));
        List<Split> splitList = splitService.getSplitsByExpenses(expenses);
        List<ExpenseBalanceDetail> details = new ArrayList<>();

        Map<String, List<Split>> splitsByExpense = splitList.stream()
                .collect(Collectors.groupingBy(s -> s.getExpense().getId()));

        for (Expense expense : expenses) {
            ExpenseBalanceDetail detail = new ExpenseBalanceDetail();
            detail.setExpenseId(expense.getId());
            detail.setRole("creditor");

            Map<String, Double> balances = new HashMap<>();

            List<Split> splits = splitsByExpense.get(expense.getId());
            if (splits != null) {
                for (Split split : splits) {
                    if (split.getUser().getId().equals(userId)) {
                        continue;
                    }

                    double settled = (split.getSettledAmount() != null) ? split.getSettledAmount() : 0.0;
                    double remaining = split.getSplitAmount() - settled;

                    if (remaining > 0) {
                        balances.put(split.getUser().getId(),
                                balances.getOrDefault(split.getUser().getId(), 0.0) + remaining);
                    }
                }
            }

            detail.setBalances(balances);
            details.add(detail);
        }

        return details;
    }
}
