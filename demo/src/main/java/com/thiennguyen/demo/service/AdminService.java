package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    // 1. Đếm tổng số user cho Dashboard
    long getTotalUsers();

    // 2. Lấy danh sách hiển thị
    List<User> getAllUsers();

    // 3. Xử lý logic khóa/mở khóa tài khoản
    void toggleUserStatus(Integer targetUserId, Integer loggedInAdminId) throws Exception;

    // 4. Lấy thông tin chi tiết của 1 User (Dành cho nút Con Mắt)
    User getUserById(Integer id);

    // 5. Cập nhật Quyền và Trạng thái
    void updateRoleAndStatus(Integer id, String role, String status) throws Exception;

    // 6. NĂNG CẤP: Lấy danh sách có phân trang và bộ lọc
    Page<User> getPaginatedUsers(int pageNo, int pageSize, String keyword, String status);
}