package com.example.designsplitwise.repository;

import com.example.designsplitwise.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
