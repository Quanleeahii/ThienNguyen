package com.thiennguyen.demo.service;

import jakarta.mail.internet.MimeMessage; // Dùng cho email HTML
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác thực OTP - Nền tảng Ươm Mầm");
        message.setText("Chào bạn,\n\n"
                + "Bạn vừa yêu cầu mã xác thực từ Ươm Mầm. Dưới đây là mã OTP của bạn:\n\n"
                + "👉 MÃ OTP: " + otp + " 👈\n\n"
                + "Vui lòng nhập mã này lên trang web để tiếp tục. Tuyệt đối không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ Ươm Mầm");
        mailSender.send(message);
        System.out.println("Đã gửi email OTP thành công tới: " + toEmail);
    }

    @Async
    @Override
    public void sendThankYouEmail(String toEmail, String donorName, Double amount, String campaignTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Biên lai quyên góp & Thư Cảm Ơn từ Nền tảng Ươm Mầm");

            NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedAmount = format.format(amount) + " VNĐ";

            String htmlMsg = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #f26b21; text-align: center;'>Cảm ơn tấm lòng của bạn! ❤️</h2>"
                    + "<p>Xin chào <b>" + donorName + "</b>,</p>"
                    + "<p>Hệ thống Ươm Mầm xin thông báo: Chúng tôi đã nhận được khoản quyên góp của bạn.</p>"
                    + "<div style='background-color: #f9f9f9; padding: 15px; border-left: 4px solid #f26b21; margin: 20px 0;'>"
                    + "  <p style='margin: 0;'><b>Chiến dịch:</b> " + campaignTitle + "</p>"
                    + "  <p style='margin: 5px 0 0 0;'><b>Số tiền:</b> <span style='color: #d95a1a; font-size: 18px; font-weight: bold;'>" + formattedAmount + "</span></p>"
                    + "</div>"
                    + "<p>Sự đóng góp của bạn là ngọn lửa ấm áp giúp thắp sáng hy vọng cho những hoàn cảnh khó khăn.</p>"
                    + "<p>Chúc bạn thật nhiều sức khỏe và bình an!</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee;'/>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);

            System.out.println("✅ Đã gửi email biên lai thành công tới: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi email biên lai: " + e.getMessage());
        }
    }
}