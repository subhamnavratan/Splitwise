package com.example.designsplitwise.controller;

import com.example.designsplitwise.dto.SettlementRequest;
import com.example.designsplitwise.service.SettlementService;
import com.example.designsplitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/splitwise")
public class SettlementController {
    private final SettlementService settlementService;

    @Autowired
    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/settle")
    public ResponseEntity<?> settle(@RequestBody SettlementRequest settlementRequest) {
        String message = settlementService.settle(settlementRequest);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
