package com.example.designsplitwise.controller;

import com.example.designsplitwise.model.User;
import com.example.designsplitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/splitwise")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create/user/{userName}/{email}")
    public ResponseEntity<?> createUser(@PathVariable String userName, @PathVariable String email) {
        String userId = userService.createUser(userName, email);
        return ResponseEntity.ok(userId);
    }
}
