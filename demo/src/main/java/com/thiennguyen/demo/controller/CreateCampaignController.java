package com.thiennguyen.demo.controller;
import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.CategoryService;
import com.thiennguyen.demo.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/tao-chien-dich")
public class CreateCampaignController {
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FileStorageService fileStorageService;
    @GetMapping("/bat-dau")
    public String showChoicePage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/dang-nhap";
        }
        return "ungho/choose-type";
    }
    @GetMapping("/ca-nhan")
    public String showPersonForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/dang-nhap";
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "ungho/create-campaign-person";
    }
    @PostMapping("/ca-nhan/luu")
    public String savePersonCampaign(
            @ModelAttribute("campaign") Campaign campaign,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ){
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/dang-nhap";
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile, "campaigns");
                campaign.setImageUrl("/upload_img/campaigns/" + fileName);
            }
            campaign.setCreator(loggedInUser);
            campaign.setType("person");
            campaign.setStatus("pending");
            campaign.setCurrentAmount(0.0);
            campaign.setDonationCount(0);
            campaignService.saveCampaign(campaign);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo chiến dịch thành công! Vui lòng chờ Admin duyệt.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/tao-chien-dich/ca-nhan";
        }
        return "redirect:/danh-sach-chien-dich";
    }
    @GetMapping("/to-chuc")
    public String showOrgForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/dang-nhap";
        return "ungho/create-org";
    }
}