package com.example.designsplitwise.dto;

import com.example.designsplitwise.model.Group;
import com.example.designsplitwise.model.SplitType;
import com.example.designsplitwise.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpenseResponse {
    private String id;
    private String name;
    private String description;
    private Double amount;
    private SplitType splitType;
    private User createdBy;
    private Group group;
    private List<SplitResponse> splits;
}
