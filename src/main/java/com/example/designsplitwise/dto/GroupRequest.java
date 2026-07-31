package com.example.designsplitwise.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupRequest {
    private String groupName;
    private String groupDescription;
    private List<String> groupMembers;
}
