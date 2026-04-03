package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.User;
public interface AuthService {
    String registerUser(String fullName, String email, String password) throws Exception;
    User loginUser(String email, String password) throws Exception;
    void activateUser(String email);
    void updatePassword(String email, String newPassword);
    String generateAndSendOtp(String email);
    void validateAndProcessOtp(String sessionOtp, String enteredOtp, Long creationTime, Integer attempts, String email, String otpType, String newPassword) throws Exception;
}