package com.example.designsplitwise.service;

import com.example.designsplitwise.dto.GroupRequest;
import com.example.designsplitwise.model.Group;
import com.example.designsplitwise.model.User;
import com.example.designsplitwise.repository.GroupRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    private final GroupRespository groupRespository;
    private final UserService userService;

    @Autowired
    public GroupService(GroupRespository groupRespository, UserService userService) {
        this.groupRespository = groupRespository;
        this.userService = userService;
    }

    public String createGroup(GroupRequest groupRequest) {
        List<User> users = userService.getUsers(groupRequest.getGroupMembers());
        Group group = new Group();
        group.setName(groupRequest.getGroupName());
        group.setDescription(groupRequest.getGroupDescription());
        group.setGroupMembers(users);
        Group newGroup = groupRespository.save(group);
        return newGroup.getId();
    }

    public Group getGroup(String id) {
        return groupRespository.findById(id).get();
    }
}
