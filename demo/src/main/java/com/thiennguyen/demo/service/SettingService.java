package com.thiennguyen.demo.service;

public interface SettingService {
    // Hàm xử lý logic xóa tài khoản
    void deleteAccount(Integer userId, String password) throws Exception;
}