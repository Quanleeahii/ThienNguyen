package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Hiển thị trang Hồ sơ cá nhân (Dùng giao diện HTML truyền thống)
     */
    @GetMapping
    public String showProfilePage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/dang-nhap";
        }

        // Luôn lấy dữ liệu mới nhất từ DB để đảm bảo thông tin hiển thị chính xác
        User currentUser = profileService.getUserById(loggedInUser.getId());
        model.addAttribute("user", currentUser);

        return "user/profile";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @ModelAttribute("user") User updatedData,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Phiên đăng nhập đã hết hạn!"));
        }

        try {
            // 2. Thực hiện cập nhật logic (Lưu thông tin + xử lý file ảnh)
            User savedUser = profileService.updateProfile(loggedInUser.getId(), updatedData, avatarFile);

            // 3. Cập nhật lại "Chiếc thẻ bài" Session để các trang khác (như Header) ăn theo dữ liệu mới
            session.setAttribute("loggedInUser", savedUser);

            // 4. Trả về phản hồi thành công kèm tên file ảnh mới để JS cập nhật giao diện
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Cập nhật hồ sơ thành công!",
                    "newAvatar", savedUser.getAvatar() != null ? savedUser.getAvatar() : ""
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Đã xảy ra lỗi: " + e.getMessage()
            ));
        }
    }
}