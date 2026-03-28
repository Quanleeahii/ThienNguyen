package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.User;
import java.util.List;
public interface UserService {
    List<User> getAllUsers();
    User saveUser(User user);
    User getUserById(Integer id);
}