package com.thiennguyen.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

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
}