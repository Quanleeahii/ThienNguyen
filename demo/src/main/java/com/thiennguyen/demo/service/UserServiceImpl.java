package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}