package com.thiennguyen.demo;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Category;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.CampaignRepository;
import com.thiennguyen.demo.repository.CategoryRepository;
import com.thiennguyen.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataSeeder implements CommandLineRunner {
    private final CampaignRepository campaignRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public DataSeeder(CampaignRepository campaignRepository,
                      CategoryRepository categoryRepository,
                      UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        campaignRepository.deleteAll();
        categoryRepository.deleteAll();
        System.out.println(">>> ĐÃ QUÉT SẠCH RÁC TRONG DATABASE (CAMPAIGN & CATEGORY)!");
        User dummyUser;
        if (userRepository.count() == 0) {
            dummyUser = new User();
            dummyUser.setFullName("Lê Anh Quân");
            dummyUser.setDob(LocalDate.of(2006, 9, 13));
            dummyUser.setEmail("quanlee1309@gmail.com");
            dummyUser.setPhone("0963477434");
            dummyUser.setAddress("Đông Anh - Hà Nội");
            dummyUser.setRole("ADMIN");
            dummyUser.setStatus("ACTIVE");
            dummyUser.setCreatedAt(LocalDateTime.now());
            dummyUser = userRepository.save(dummyUser);
        } else {
            dummyUser = userRepository.findAll().get(0);
        }
        String[][] categoryData = {
                {"Xóa nghèo", "fa-solid fa-hand-holding-dollar"}, {"Xóa đói", "fa-solid fa-shield-heart"},
                {"Trẻ em", "fa-regular fa-face-smile"}, {"Người cao tuổi", "fa-solid fa-person-cane"},
                {"Người nghèo", "fa-regular fa-face-dizzy"}, {"Người khuyết tật", "fa-solid fa-wheelchair"},
                {"Bệnh hiểm nghèo", "fa-solid fa-virus"}, {"Dân tộc thiểu số", "fa-solid fa-users"},
                {"Lao động di cư", "fa-solid fa-shoe-prints"}, {"Người vô gia cư", "fa-solid fa-fire"},
                {"Môi trường", "fa-solid fa-seedling"}, {"Khác", "fa-solid fa-hand-holding-heart"},
                {"Giáo dục", "fa-solid fa-graduation-cap"}, {"Thiên tai", "fa-solid fa-earth-americas"}
        };

        for (String[] data : categoryData) {
            Category cat = new Category();
            cat.setName(data[0]);
            cat.setIcon(data[1]);
            categoryRepository.save(cat);
        }
        System.out.println(">>> Đã nạp lại 14 danh mục sạch đẹp.");
        saveC(createC("Áo ấm vùng cao", "images/hinh-anh-tre-em.jpg", 150000000.0, 150000000.0, "2025-10-01", "2025-12-31", "Điện Biên", "org", "Trẻ em", dummyUser));
        saveC(createC("Bữa trưa có thịt cho em", "images/hinh-anh-tre-em.jpg", 50000000.0, 25000000.0, "2026-02-01", "2026-06-30", "Hà Giang", "person", "Trẻ em", dummyUser));
        saveC(createC("Xe đạp cùng em đến trường", "images/hinh-anh-tre-em.jpg", 120000000.0, 45000000.0, "2026-04-01", "2026-08-15", "Nghệ An", "org", "Trẻ em", dummyUser));
        saveC(createC("Sữa dinh dưỡng vùng sâu", "images/hinh-anh-tre-em.jpg", 200000000.0, 80000000.0, "2026-01-15", "2026-12-31", "Kon Tum", "person", "Trẻ em", dummyUser));
        saveC(createC("Bảo trợ trẻ em mồ côi", "images/hinh-anh-tre-em.jpg", 500000000.0, 250000000.0, "2026-03-01", "2026-12-31", "Quảng Bình", "person", "Trẻ em", dummyUser));
        saveC(createC("Tủ sách thiên thần", "images/hinh-anh-tre-em.jpg", 20000000.0, 18000000.0, "2026-02-10", "2026-05-10", "Hà Nội", "org", "Trẻ em", dummyUser));

        // Người khuyết tật
        saveC(createC("Chiếc xe lăn mơ ước", "images/hinh-anh-tre-em.jpg", 150000000.0, 40000000.0, "2026-02-01", "2026-08-31", "Toàn quốc", "org", "Người khuyết tật", dummyUser));
        saveC(createC("Dạy nghề cho người khiếm thị", "images/hinh-anh-tre-em.jpg", 80000000.0, 20000000.0, "2026-04-01", "2026-10-01", "Thái Bình", "person", "Người khuyết tật", dummyUser));
        saveC(createC("Gậy trắng dẫn đường", "images/hinh-anh-tre-em.jpg", 50000000.0, 50000000.0, "2025-09-01", "2025-10-31", "Hà Nội", "org", "Người khuyết tật", dummyUser));

        // Bệnh hiểm nghèo
        saveC(createC("Quỹ mổ tim cho em", "images/hinh-anh-tre-em.jpg", 500000000.0, 120000000.0, "2026-03-01", "2026-06-30", "Hà Nội", "org", "Bệnh hiểm nghèo", dummyUser));
        saveC(createC("Viện phí nhi đồng ung thư", "images/hinh-anh-tre-em.jpg", 1000000000.0, 300000000.0, "2026-03-01", "2026-10-31", "Hà Nội", "person", "Bệnh hiểm nghèo", dummyUser));

        // Môi trường
        saveC(createC("Trồng 1 triệu cây xanh", "images/hinh-anh-tre-em.jpg", 500000000.0, 200000000.0, "2026-02-01", "2026-12-31", "Ninh Bình", "org", "Môi trường", dummyUser));

        // Giáo dục
        saveC(createC("Thư viện container cho em", "images/hinh-anh-tre-em.jpg", 120000000.0, 45000000.0, "2026-02-15", "2026-05-15", "Lào Cai", "person", "Giáo dục", dummyUser));

        System.out.println(">>> Đã nạp lại 13 chiến dịch mẫu chuẩn gắn với User.");
    }
    private Campaign createC(String title, String img, Double target, Double current,
                             String start, String end, String loc, String type,
                             String categoryName, User creator) {
        Campaign c = new Campaign();
        c.setTitle(title);
        c.setImageUrl(img);
        c.setTargetAmount(target);
        c.setCurrentAmount(current);
        c.setStartDate(LocalDate.parse(start).atStartOfDay());
        c.setEndDate(LocalDate.parse(end).atStartOfDay());
        c.setLocationName(loc);
        c.setCategory(categoryRepository.findByName(categoryName));
        c.setType(type);
        c.setCreator(creator);
        c.setDonationCount((int)(Math.random() * 100) + 10);
        c.setStatus("active");
        return c;
    }

    private void saveC(Campaign c) {
        campaignRepository.save(c);
    }
}