package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.thiennguyen.demo.repository.UserRepository userRepository;

    @GetMapping("/dang-nhap")
    public String showLoginForm(HttpSession session) {
        // Kiểm tra xem người dùng đã có Thẻ VIP (đã đăng nhập) chưa
        User user = (User) session.getAttribute("loggedInUser");

        if (user != null) {
            // Nếu đã đăng nhập rồi, bẻ lái không cho vào trang Đăng nhập nữa
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard"; // Admin thì về Dashboard
            } else {
                return "redirect:/danh-sach-chien-dich"; // User thường thì ra ds chiến dịch
            }
        }
        // Nếu chưa đăng nhập thì mới hiện form
        return "auth/login";
    }

    @GetMapping("/dang-ky")
    public String showRegisterForm(HttpSession session) {
        // Tương tự cho trang Đăng ký
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/danh-sach-chien-dich";
            }
        }
        return "auth/register";
    }

    @PostMapping("/dang-ky")
    public String processRegister(@RequestParam("fullName") String fullName,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model, HttpSession session) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }

        try {
            String otp = authService.registerUser(fullName, email, password);

            session.setAttribute("resetEmail", email);
            session.setAttribute("resetOtp", otp);
            session.setAttribute("otpType", "REGISTER");
            session.setAttribute("otpCreationTime", System.currentTimeMillis());
            session.setAttribute("otpAttempts", 0);

            return "redirect:/xac-thuc-otp";

        } catch (Exception e) {
            model.addAttribute("error", "Không thể xử lý: " + e.getMessage() + " (Vui lòng kiểm tra lại Email)");
            return "auth/register";
        }
    }

    @PostMapping("/dang-nhap")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session, Model model) {
        try {
            User user = authService.loginUser(email, password);
            session.setAttribute("loggedInUser", user);

            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/danh-sach-chien-dich";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/dang-xuat")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/dang-nhap?logout=true";
    }

    @GetMapping("/quen-mat-khau")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/quen-mat-khau")
    public String processForgotPassword(@RequestParam("email") String email, HttpSession session, Model model) {
        java.util.Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email này chưa được đăng ký trong hệ thống!");
            return "auth/forgot-password";
        }

        User user = userOpt.get();

        if ("DELETED".equals(user.getStatus())) {
            model.addAttribute("error", "Tài khoản này đã bị xóa. Không thể khôi phục mật khẩu!");
            return "auth/forgot-password";
        }

        if ("INACTIVE".equals(user.getStatus())) {
            model.addAttribute("error", "Tài khoản chưa được kích hoạt. Vui lòng đăng ký lại!");
            return "auth/forgot-password";
        }

        try {
            session.setAttribute("resetEmail", email);

            // ==========================================
            // VÁ LỖI Ở ĐÂY: Phát "thẻ ra vào" để đi tiếp
            // ==========================================
            session.setAttribute("otpType", "FORGOT_PASSWORD");

            return "redirect:/dat-lai-mat-khau";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "auth/forgot-password";
        }
    }

    // =================================================================
    // BẢN VÁ LỖI TẠI ĐÂY: KHÓA CHẶT ĐƯỜNG DẪN ĐỔI MẬT KHẨU
    // =================================================================
    @GetMapping("/dat-lai-mat-khau")
    public String showResetPasswordForm(HttpSession session) {
        // 1. Kiểm tra xem có email không
        if (session.getAttribute("resetEmail") == null) {
            return "redirect:/quen-mat-khau";
        }

        // 2. Kiểm tra xem có ĐÚNG là đang đi luồng Quên mật khẩu không
        String otpType = (String) session.getAttribute("otpType");
        if (!"FORGOT_PASSWORD".equals(otpType)) {
            // Nếu otpType là "REGISTER" (Đang đăng ký) thì sút về trang đăng nhập
            return "redirect:/dang-nhap";
        }

        return "auth/reset-password";
    }

    @PostMapping("/dat-lai-mat-khau")
    public String processResetPassword(@RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       HttpSession session, Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            return "auth/reset-password";
        }

        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/quen-mat-khau";

        String otp = authService.generateAndSendOtp(email);
        session.setAttribute("tempPassword", password);
        session.setAttribute("resetOtp", otp);
        session.setAttribute("otpType", "FORGOT_PASSWORD");
        session.setAttribute("otpCreationTime", System.currentTimeMillis());
        session.setAttribute("otpAttempts", 0);

        return "redirect:/xac-thuc-otp";
    }

    @GetMapping("/xac-thuc-otp")
    public String showOtpForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null || session.getAttribute("resetOtp") == null) return "redirect:/dang-nhap";
        model.addAttribute("email", email);
        return "auth/otp-verification";
    }

    @PostMapping("/xac-thuc-otp")
    public String processOtp(@RequestParam("otp1") String o1, @RequestParam("otp2") String o2,
                             @RequestParam("otp3") String o3, @RequestParam("otp4") String o4,
                             @RequestParam("otp5") String o5, @RequestParam("otp6") String o6,
                             HttpSession session, Model model) {

        String sessionOtp = (String) session.getAttribute("resetOtp");
        String email = (String) session.getAttribute("resetEmail");
        String otpType = (String) session.getAttribute("otpType");
        Long creationTime = (Long) session.getAttribute("otpCreationTime");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");

        if (sessionOtp == null || email == null || creationTime == null || attempts == null) {
            return "redirect:/dang-nhap";
        }

        String enteredOtp = o1 + o2 + o3 + o4 + o5 + o6;
        String newPassword = (String) session.getAttribute("tempPassword");

        try {
            authService.validateAndProcessOtp(sessionOtp, enteredOtp, creationTime, attempts, email, otpType, newPassword);
            session.invalidate();
            return "redirect:/dang-nhap?success=true";

        } catch (Exception e) {
            String errorMessage = e.getMessage();

            if ("EXPIRED".equals(errorMessage)) {
                session.invalidate();
                model.addAttribute("error", "Mã OTP đã hết hạn sau 5 phút. Vui lòng thử lại!");
                return "auth/register";

            } else if ("LOCKED".equals(errorMessage)) {
                session.invalidate();
                model.addAttribute("error", "Bạn đã nhập sai 3 lần. Phiên giao dịch bị khóa để bảo mật!");
                return "auth/register";

            } else if ("INVALID".equals(errorMessage)) {
                attempts++;
                session.setAttribute("otpAttempts", attempts);
                model.addAttribute("error", "Mã OTP không chính xác! Bạn còn " + (3 - attempts) + " lần thử.");
                model.addAttribute("email", email);
                return "auth/otp-verification";

            } else {
                model.addAttribute("error", "Lỗi hệ thống: " + errorMessage);
                model.addAttribute("email", email);
                return "auth/otp-verification";
            }
        }
    }
}