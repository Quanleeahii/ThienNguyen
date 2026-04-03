package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.entity.User;
import com.thiennguyen.demo.repository.CampaignRepository;
import com.thiennguyen.demo.repository.DonationRepository;
import com.thiennguyen.demo.service.AdminService;
import com.thiennguyen.demo.service.CampaignService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private CampaignService campaignService;

    // --- 1. DASHBOARD ---
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        Double totalMoney = donationRepository.sumTotalAmount();
        model.addAttribute("totalMoney", totalMoney != null ? totalMoney : 0);
        model.addAttribute("totalDonors", donationRepository.countByStatus("PAID"));
        model.addAttribute("totalUsers", adminService.getTotalUsers());
        model.addAttribute("totalCampaigns", campaignService.getAllCampaigns().size());
        model.addAttribute("totalReports", 2);
        return "admin/dashboard";
    }

    // --- 2. TẦNG 1: QUẢN LÝ SAO KÊ & GIẢI NGÂN (MỚI) ---
    @GetMapping("/manage-campaign-statements")
    public String manageCampaignStatements(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "disburseStatus", required = false) String disburseStatus,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        model.addAttribute("activePage", "statements");

        int pageSize = 10; // 10 chiến dịch mỗi trang
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String fType = (type == null || "all".equals(type) || "".equals(type)) ? null : type;
        String fStatus = (status == null || "".equals(status)) ? null : status;
        String fDisburse = (disburseStatus == null || "".equals(disburseStatus)) ? null : disburseStatus;

        Page<Campaign> page = campaignRepository.filterForAdmin(keyword, fStatus, fType, fDisburse, pageable);

        // Gửi dữ liệu phân trang xuống HTML
        model.addAttribute("campaigns", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        // Quan trọng: Gửi lại các biến lọc để giữ trạng thái cho nút Phân trang
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDisburse", disburseStatus);
        model.addAttribute("keyword", keyword);

        return "admin/manage-campaign-statements";
    }

    // XỬ LÝ GIẢI NGÂN
    @PostMapping("/campaigns/disburse")
    public String handleDisbursement(
            @RequestParam("campaignId") Integer campaignId,
            @RequestParam("evidence") MultipartFile evidenceFile,
            @RequestParam("note") String note) {
        try {
            Campaign campaign = campaignRepository.findById(campaignId).orElseThrow();
            if (!evidenceFile.isEmpty()) {
                String fileName = "bill_" + campaignId + "_" + System.currentTimeMillis() + ".jpg";
                Path uploadPath = Paths.get("src/main/resources/static/upload_img/bills/");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                try (InputStream inputStream = evidenceFile.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    campaign.setDisbursementEvidence(fileName);
                }
            }
            campaign.setDisbursementStatus("DISBURSED");
            campaign.setDisbursedAt(LocalDateTime.now());
            campaign.setDisbursementNote(note);
            campaignRepository.save(campaign);
            return "redirect:/admin/manage-campaign-statements?success=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-campaign-statements?error=true";
        }
    }

    // --- 3. TẦNG 2: SỔ CÁI / SAO KÊ CHI TIẾT ---
    @GetMapping("/donations")
    public String manageDonations(
            @RequestParam(value = "campaignId", required = false) Integer campaignId,
            @RequestParam(value = "isGrouped", defaultValue = "false") boolean isGrouped,
            Model model) {
        model.addAttribute("activePage", "donations");
        if (campaignId != null) {
            Campaign campaign = campaignService.getCampaignById(campaignId);
            model.addAttribute("selectedCampaign", campaign);
            model.addAttribute("donations", donationRepository.findByCampaignIdOrderByDonationTimeDesc(campaignId));
        } else {
            model.addAttribute("donations", donationRepository.findAllByOrderByDonationTimeDesc());
        }
        return "admin/manage-donations";
    }

    // --- 4. QUẢN LÝ NGƯỜI DÙNG (CỦA ÔNG - ĐÃ TRẢ LẠI ĐỦ) ---
    @GetMapping("/users")
    public String manageUsers(
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        model.addAttribute("activePage", "users");
        int pageSize = 10;
        Page<User> page = adminService.getPaginatedUsers(pageNo, pageSize, keyword, status);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("users", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/manage-users";
    }

    @GetMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("id") Integer targetUserId, HttpSession session) {
        try {
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            adminService.toggleUserStatus(targetUserId, loggedInAdmin.getId());
            return "redirect:/admin/users";
        } catch (Exception e) {
            return "redirect:/admin/users?error=" + e.getMessage();
        }
    }

    @GetMapping("/users/detail")
    public String viewUserDetail(@RequestParam("id") Integer id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("u", user);
        return "admin/user-detail";
    }

    @PostMapping("/users/update")
    public String updateUserDetails(@RequestParam("id") Integer id,
                                    @RequestParam("role") String role,
                                    @RequestParam("status") String status) {
        try {
            adminService.updateRoleAndStatus(id, role, status);
            return "redirect:/admin/users/detail?id=" + id + "&success=true";
        } catch (Exception e) {
            return "redirect:/admin/users/detail?id=" + id + "&error=true";
        }
    }
}