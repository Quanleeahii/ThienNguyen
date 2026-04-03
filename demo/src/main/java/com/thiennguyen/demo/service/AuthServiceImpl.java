package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import com.thiennguyen.demo.service.AuthService;
import com.thiennguyen.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Random;
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Override
    public String registerUser(String fullName, String email, String password) throws Exception {
        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // 1. Nếu tài khoản đang hoạt động bình thường thì báo lỗi
            if ("ACTIVE".equals(existingUser.getStatus())) {
                throw new Exception("Email này đã được sử dụng!");
            }

            // 2. Nếu tài khoản là INACTIVE (đăng ký dở dang)
            // HOẶC là DELETED (đã xóa trước đó)
            // => Xóa bản ghi cũ này đi để cho phép đăng ký mới hoàn toàn
            else if ("INACTIVE".equals(existingUser.getStatus()) || "DELETED".equals(existingUser.getStatus())) {
                userRepository.delete(existingUser);
            }
        }
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        String generatedUsername = email.substring(0, email.indexOf("@"));
        newUser.setUsername(generatedUsername);
        newUser.setRole("ROLE_USER");
        newUser.setStatus("INACTIVE");
        userRepository.save(newUser);
        return generateAndSendOtp(email);
    }
    @Override
    public User loginUser(String email, String password) throws Exception {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Email hoặc mật khẩu không chính xác!"));

        // --- BƯỚC CHUYÊN NGHIỆP: KIỂM TRA TRẠNG THÁI TÀI KHOẢN TRƯỚC ---

        // A. Chặn tài khoản bị BANNED (Mở khóa comment rồi nhé!)
        if ("BANNED".equals(user.getStatus())) {
            throw new Exception("Tài khoản của bạn đã bị khóa do vi phạm quy định!");
        }

        // B. Chặn tài khoản đã bị XÓA MỀM (Soft Delete)
        if ("DELETED".equals(user.getStatus())) {
            throw new Exception("Tài khoản này đã bị xóa hoặc ngừng hoạt động!");
        }

        // C. Chặn tài khoản chưa kích hoạt (Chưa nhập OTP)
        if ("INACTIVE".equals(user.getStatus())) {
            throw new Exception("Tài khoản chưa kích hoạt! Vui lòng dùng email này đăng ký lại để nhận mã OTP.");
        }

        // 2. Vượt qua trạm gác an toàn rồi mới kiểm tra mật khẩu
        if (!user.getPassword().equals(password)) {
            throw new Exception("Email hoặc mật khẩu không chính xác!");
        }

        // Mọi thứ OK mới trả về User để lưu vào Session
        return user;
    }
    @Override
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email).get();
        user.setStatus("ACTIVE");
        userRepository.save(user);
    }
    @Override
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).get();
        user.setPassword(newPassword);
        userRepository.save(user);
    }
    @Override
    public String generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        emailService.sendOtpEmail(email, otp);
        System.out.println(">>> ĐÃ GỬI MAIL OTP ĐẾN: " + email);
        return otp;
    }
    @Override
    public void validateAndProcessOtp(String sessionOtp, String enteredOtp, Long creationTime, Integer attempts, String email, String otpType, String newPassword) throws Exception {

        // 1. Kiểm tra thời gian (5 phút = 300.000 ms)
        long currentTime = System.currentTimeMillis();
        if (currentTime - creationTime > 300000) {
            throw new Exception("EXPIRED"); // Ném ra mã lỗi Hết hạn
        }

        // 2. Kiểm tra tính chính xác và số lần sai
        if (!sessionOtp.equals(enteredOtp)) {
            if (attempts >= 2) { // Nếu đã sai 2 lần trước đó, lần này là lần 3 -> Khóa
                throw new Exception("LOCKED");
            }
            throw new Exception("INVALID"); // Sai nhưng chưa quá 3 lần
        }

        // 3. Nếu mọi thứ hợp lệ, tiến hành xử lý
        if ("REGISTER".equals(otpType)) {
            this.activateUser(email);
        } else if ("FORGOT_PASSWORD".equals(otpType)) {
            this.updatePassword(email, newPassword);
        }
    }
}