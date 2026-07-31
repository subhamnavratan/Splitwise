package com.example.designsplitwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    Double amount;
    String description;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    User createdBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

    @Enumerated(EnumType.STRING)
    SplitType type;
}
