package com.example.designsplitwise.dto;

import com.example.designsplitwise.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SplitResponse {
    private String id;
    private User user;
    private Double amount;
    private Double settlementAmount;
    private boolean isSettled;
    private boolean isOwing;
}
