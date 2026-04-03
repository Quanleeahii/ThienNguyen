package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void toggleUserStatus(Integer targetUserId, Integer loggedInAdminId) throws Exception {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        if (targetUser.getId().equals(loggedInAdminId)) {
            throw new Exception("cannot_ban_self");
        }

        if ("ACTIVE".equals(targetUser.getStatus())) {
            targetUser.setStatus("BANNED");
        } else if ("BANNED".equals(targetUser.getStatus())) {
            targetUser.setStatus("ACTIVE");
        }

        userRepository.save(targetUser);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng này!"));
    }

    @Override
    public void updateRoleAndStatus(Integer id, String role, String status) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));
        user.setRole(role);
        user.setStatus(status);
        userRepository.save(user);
    }

    // --- NĂNG CẤP: Bộ não Phân trang & Lọc ---
    @Override
    public Page<User> getPaginatedUsers(int pageNo, int pageSize, String keyword, String status) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize); // Spring Boot đếm trang từ 0

        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasStatus = (status != null && !status.trim().isEmpty());

        if (hasKeyword && hasStatus) {
            return userRepository.findByStatusAndKeyword(status, keyword, pageable);
        } else if (hasStatus) {
            return userRepository.findByStatus(status, pageable);
        } else if (hasKeyword) {
            return userRepository.findByKeyword(keyword, pageable);
        } else {
            return userRepository.findAllActive(pageable); // Mặc định không hiện người DELETED
        }
    }
}