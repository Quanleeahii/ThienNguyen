package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.User;
import org.springframework.web.multipart.MultipartFile;
public interface ProfileService {
    User getUserById(Integer id);
    User updateProfile(Integer userId, User updatedData, MultipartFile avatarFile) throws Exception;
}