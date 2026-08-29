package com.example.designsplitwise.service;

import com.example.designsplitwise.model.User;
import com.example.designsplitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public List<User> getUsers(List<String> userIds){
        Iterable<User> userIterable = userRepository.findAllById(userIds);
        List<User> users = new ArrayList<>();
        userIterable.forEach(users::add);
        return users;
    }

    public User getUser(String id){
        return userRepository.findById(id).get();
    }
}
