package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import com.thiennguyen.demo.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Random;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Gọi Bưu tá vào làm việc
    @Autowired
    private EmailService emailService;

    @GetMapping("/dang-nhap")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) return "redirect:/";
        return "auth/login";
    }

    @GetMapping("/dang-ky")
    public String showRegisterForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) return "redirect:/";
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

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {

            if ("ACTIVE".equals(existingUser.get().getStatus())) {
                model.addAttribute("error", "Email này đã được sử dụng!");
                return "auth/register";
            } else {

                userRepository.delete(existingUser.get());
            }
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole("ROLE_USER");
        newUser.setStatus("INACTIVE");

        userRepository.save(newUser);


        String otp = String.format("%06d", new Random().nextInt(999999));
        emailService.sendOtpEmail(email, otp);
        System.out.println(">>> ĐÃ GỬI MAIL OTP ĐĂNG KÝ ĐẾN: " + email);

        session.setAttribute("resetEmail", email);
        session.setAttribute("resetOtp", otp);
        session.setAttribute("otpType", "REGISTER"); // Đánh dấu là OTP của Đăng ký

        return "redirect:/xac-thuc-otp";
    }


    @PostMapping("/dang-nhap")
    public String processLogin(@RequestParam("username") String email,
                               @RequestParam("password") String password,
                               HttpSession session, Model model) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {


                if ("INACTIVE".equals(user.getStatus())) {
                    model.addAttribute("error", "Tài khoản chưa kích hoạt! Vui lòng dùng email này đăng ký lại để nhận mã OTP.");
                    return "auth/login";
                }

                session.setAttribute("loggedInUser", user);
                return "redirect:/danh-sach-chien-dich";
            }
        }

        model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
        return "auth/login";
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
        if (userRepository.findByEmail(email).isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản với email này!");
            return "auth/forgot-password";
        }
        session.setAttribute("resetEmail", email);
        return "redirect:/dat-lai-mat-khau";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String showResetPasswordForm(HttpSession session) {
        if (session.getAttribute("resetEmail") == null) return "redirect:/quen-mat-khau";
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


        String otp = String.format("%06d", new Random().nextInt(999999));
        emailService.sendOtpEmail(email, otp);
        System.out.println(">>> ĐÃ GỬI MAIL OTP ĐỔI MẬT KHẨU ĐẾN: " + email);

        session.setAttribute("tempPassword", password);
        session.setAttribute("resetOtp", otp);
        session.setAttribute("otpType", "FORGOT_PASSWORD");

        return "redirect:/xac-thuc-otp";
    }


    @GetMapping("/xac-thuc-otp")
    public String showOtpForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null || session.getAttribute("resetOtp") == null) return "redirect:/quen-mat-khau";
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
        String enteredOtp = o1 + o2 + o3 + o4 + o5 + o6;

        if (sessionOtp == null || email == null) return "redirect:/dang-nhap";

        if (!sessionOtp.equals(enteredOtp)) {
            model.addAttribute("error", "Mã OTP không chính xác!");
            model.addAttribute("email", email);
            return "auth/otp-verification";
        }


        User user = userRepository.findByEmail(email).get();

        if ("REGISTER".equals(otpType)) {

            user.setStatus("ACTIVE");
            userRepository.save(user);
        } else if ("FORGOT_PASSWORD".equals(otpType)) {

            String newPassword = (String) session.getAttribute("tempPassword");
            user.setPassword(newPassword);
            userRepository.save(user);
        }


        session.invalidate();
        return "redirect:/dang-nhap?success=true";
    }
}