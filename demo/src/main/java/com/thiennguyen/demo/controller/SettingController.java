package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.service.SettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    // Hiển thị trang cài đặt
    @GetMapping
    public String showSettingsPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/dang-nhap";
        }
        return "user/setting";
    }

    // Xử lý logic khi bấm nút "Xóa vĩnh viễn"
    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam("password") String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // 1. Lấy thông tin người dùng đang đăng nhập
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/dang-nhap";
        }

        try {
            // 2. Gọi Service để xử lý xóa
            settingService.deleteAccount(loggedInUser.getId(), password);

            // 3. Nếu thành công -> Hủy toàn bộ Session (Đăng xuất)
            session.invalidate();

            // 4. Đuổi về trang Đăng nhập kèm theo mã thông báo
            return "redirect:/dang-nhap?deleted=true";

        } catch (Exception e) {
            // 5. Nếu mật khẩu sai -> Trả về lỗi đỏ trên màn hình Cài đặt
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/settings";
        }
    }
}