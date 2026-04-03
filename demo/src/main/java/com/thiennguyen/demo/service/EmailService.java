package com.thiennguyen.demo.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendThankYouEmail(String toEmail, String donorName, Double amount, String campaignTitle);
}