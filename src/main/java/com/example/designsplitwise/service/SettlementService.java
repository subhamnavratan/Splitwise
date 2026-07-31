package com.example.designsplitwise.service;

import com.example.designsplitwise.dto.SettlementRequest;
import com.example.designsplitwise.model.Settlement;
import com.example.designsplitwise.model.Split;
import com.example.designsplitwise.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final SplitService splitService;

    @Autowired
    public SettlementService(SettlementRepository settlementRepository, UserService userService, ExpenseService expenseService, SplitService splitService) {
        this.settlementRepository = settlementRepository;
        this.userService = userService;
        this.expenseService = expenseService;
        this.splitService = splitService;
    }

    public String settle(SettlementRequest settlementRequest) {
        Split split = splitService.getSplitByExpenseIdAndUserId(settlementRequest.getExpenseId(), settlementRequest.getPaidByUserId());
        double newSettledAmount = split.getSettledAmount() + settlementRequest.getAmount();
        split.setSettledAmount(newSettledAmount);
        splitService.saveSplit(split);

        Settlement settlement = new Settlement();
        settlement.setAmount(settlementRequest.getAmount());
        settlement.setExpense(expenseService.getExpenseById(settlementRequest.getExpenseId()));
        settlement.setPaidBy(userService.getUser(settlementRequest.getPaidByUserId()));
        settlement.setPaidTo(userService.getUser(settlementRequest.getPaidToUserId()));
        settlementRepository.save(settlement);

        return "Settlement successful";
    }
}
