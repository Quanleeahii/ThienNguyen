package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.UserRepository;
import com.thiennguyen.demo.service.FileStorageService;
import com.thiennguyen.demo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    @Override
    public User updateProfile(Integer userId, User updatedData, MultipartFile avatarFile) throws Exception {
        // 1. Tìm user hiện tại trong Database
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy thông tin người dùng!"));
        existingUser.setFullName(updatedData.getFullName());
        existingUser.setGender(updatedData.getGender());
        existingUser.setDob(updatedData.getDob());
        existingUser.setPhone(updatedData.getPhone());
        existingUser.setContactEmail(updatedData.getContactEmail());
        existingUser.setLinkFb(updatedData.getLinkFb());
        existingUser.setLinkYt(updatedData.getLinkYt());
        existingUser.setLinkTiktok(updatedData.getLinkTiktok());
        existingUser.setBio(updatedData.getBio());
        existingUser.setAddress(updatedData.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());
        if (avatarFile != null && !avatarFile.isEmpty()) {
            if (existingUser.getAvatar() != null && !existingUser.getAvatar().isEmpty()) {
                fileStorageService.deleteFile(existingUser.getAvatar(), "avatars");
            }
            String fileName = fileStorageService.saveFile(avatarFile, "avatars");
            existingUser.setAvatar(fileName);
        }
        return userRepository.save(existingUser);
    }

}