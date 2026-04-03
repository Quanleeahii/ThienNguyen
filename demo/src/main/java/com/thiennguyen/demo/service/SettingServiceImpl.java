package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void deleteAccount(Integer userId, String password) throws Exception {
        // 1. Tìm User trong Database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Lỗi: Không tìm thấy tài khoản!"));

        // 2. Xác thực mật khẩu
        // (Nếu sau này ông dùng BCrypt thì đổi thành passwordEncoder.matches(...))
        if (!user.getPassword().equals(password)) {
            throw new Exception("Mật khẩu hiện tại không chính xác!");
        }

        // 3. Thực hiện Soft Delete (Xóa mềm)
        user.setStatus("DELETED");

        // Mẹo nâng cao: Đổi email thành một chuỗi ngẫu nhiên để giải phóng email gốc.
        // Giúp người dùng có thể dùng lại email này để đăng ký tài khoản mới sau này.
        user.setEmail("deleted_" + System.currentTimeMillis() + "_" + user.getEmail());

        // 4. Lưu lại vào DB
        userRepository.save(user);
    }
}