package com.thiennguyen.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${upload.path}")
    private String uploadRootPath;

    @Override
    public String saveFile(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. Chỉ đường đến đúng thư mục con
        Path uploadPath = Paths.get(uploadRootPath, subFolder);

        // 2. Tự động tạo nếu chưa có
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Đổi tên file bằng UUID để chống trùng
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // 4. Copy file vào ổ cứng
        Path destinationFile = uploadPath.resolve(newFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // 5. Trả về tên file mới
        return newFilename;
    }
    @Override
    public void deleteFile(String fileName, String subFolder) {
        try {
            // 1. Xác định đường dẫn đầy đủ đến file cần xóa
            Path filePath = Paths.get(uploadRootPath, subFolder).resolve(fileName);

            // 2. Kiểm tra nếu file có tồn tại thì mới xóa
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("--- ĐÃ XÓA FILE CŨ THÀNH CÔNG: " + fileName);
            }
        } catch (IOException e) {
            // Nếu lỗi (file đang bị chương trình khác mở...) thì chỉ in ra log, không làm dừng chương trình
            System.err.println("Lỗi khi xóa file: " + e.getMessage());
        }
    }
}