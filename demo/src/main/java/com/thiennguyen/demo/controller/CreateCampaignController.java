package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.CategoryService;
import com.thiennguyen.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/tao-chien-dich")
public class CreateCampaignController {

    @Autowired
    private CampaignService campaignService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/bat-dau")
    public String showChoicePage() {
        return "ungho/choose-type";
    }

    @GetMapping("/ca-nhan")
    public String showPersonForm(Model model) {
        User currentUser = userService.getUserById(1);
        model.addAttribute("user", currentUser);
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "ungho/create-campaign-person";
    }

    @PostMapping("/ca-nhan/luu")
    public String savePersonCampaign(
            @ModelAttribute("campaign") Campaign campaign,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ){
        if (imageFile != null && !imageFile.isEmpty()) {
            try {

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();


                Path uploadPath = Paths.get("upload_img", "campaigns").toAbsolutePath();

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }


                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                campaign.setImageUrl("/upload_img/campaigns/" + fileName);

            } catch (Exception e) {
                System.err.println("Lỗi upload ảnh: " + e.getMessage());
                e.printStackTrace();
            }
        }


        User creator = userService.getUserById(1);
        campaign.setCreator(creator);
        campaign.setType("person");
        campaign.setStatus("active");
        campaign.setCurrentAmount(0.0);
        campaign.setDonationCount(0);


        campaignService.saveCampaign(campaign);

        return "redirect:/danh-sach-chien-dich";
    }

    @GetMapping("/to-chuc")
    public String showOrgForm() {
        return "ungho/create-org";
    }
}