package com.example.designsplitwise.controller;

import com.example.designsplitwise.dto.GroupRequest;
import com.example.designsplitwise.model.Group;
import com.example.designsplitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/splitwise")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create/group")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest request) {
        String groupId = groupService.createGroup(request);
        return new ResponseEntity<>(groupId, HttpStatus.CREATED);
    }
}
