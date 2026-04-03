package com.thiennguyen.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /**
     * Hàm lưu file vào thư mục con
     * @param file File cần lưu
     * @param subFolder Tên thư mục con (VD: "avatars", "campaigns")
     * @return Tên file sau khi lưu (đã mã hóa)
     */
    String saveFile(MultipartFile file, String subFolder) throws IOException;
    void deleteFile(String fileName, String subFolder);
}