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

@Component
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
        User dummyUser;
        if (userRepository.count() == 0) {
            dummyUser = new User();
            dummyUser.setFullName("Lê Anh Quân");
            dummyUser.setUsername("quanlee1309");
            dummyUser.setDob(LocalDate.of(2006, 9, 13));
            dummyUser.setEmail("quanlee1309@gmail.com");
            dummyUser.setPhone("0963477434");
            dummyUser.setAddress("Đông Anh - Hà Nội");
            dummyUser.setRole("ROLE_ADMIN");
            dummyUser.setStatus("ACTIVE");
            dummyUser.setCreatedAt(LocalDateTime.now());
            dummyUser.setPassword("123456");
            dummyUser = userRepository.save(dummyUser);
            System.out.println(">>> Đã tạo User Admin mặc định.");
        } else {
            dummyUser = userRepository.findAll().get(0);
        }

        if (categoryRepository.count() == 0) {
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
            System.out.println(">>> Đã nạp 14 danh mục mặc định.");
        }

        if (campaignRepository.count() == 0) {
            saveC(createC("Áo ấm vùng cao", "images/hinh-anh-tre-em.jpg", 150000000.0, 150000000.0, "2025-10-01", "2025-12-31", "Điện Biên", "org", "Trẻ em", dummyUser));
            saveC(createC("Bữa trưa có thịt cho em", "images/hinh-anh-tre-em.jpg", 50000000.0, 25000000.0, "2026-02-01", "2026-06-30", "Hà Giang", "person", "Trẻ em", dummyUser));

            System.out.println(">>> Đã nạp lại các chiến dịch mẫu.");
        } else {
            System.out.println(">>> Database đã có chiến dịch, bỏ qua bước nạp mẫu.");
        }
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

        Category cat = categoryRepository.findByName(categoryName);
        if (cat != null) {
            c.setCategory(cat);
        }

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