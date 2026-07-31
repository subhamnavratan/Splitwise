package com.example.designsplitwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Split {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "split_user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    Expense expense;

    Double splitAmount;
    boolean isOwe;
    private Double settledAmount = 0.0;

    public boolean isSettled() {
        return settledAmount >= splitAmount;
    }
}
