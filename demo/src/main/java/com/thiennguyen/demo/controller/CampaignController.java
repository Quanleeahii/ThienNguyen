package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.repository.DonationRepository;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CategoryService categoryService;

    // Tiêm Repository để lấy danh sách người đã ủng hộ
    @Autowired
    private DonationRepository donationRepository;

    @GetMapping("/danh-sach-chien-dich")
    public String showCampaignList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Campaign> campaignPage = campaignService.getFilteredCampaigns(keyword, categoryId, status, type, pageable);
        Map<Integer, Long> categoryCounts = campaignService.getCategoryCounts(keyword, status, type);
        model.addAttribute("campaigns", campaignPage.getContent());
        model.addAttribute("totalPages", campaignPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("selectedType", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("categoryCounts", categoryCounts);
        return "ungho/campaigns";
    }

    @GetMapping("/chien-dich/{id}")
    public String showCampaignDetail(@PathVariable("id") Integer id, Model model) {
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign == null) {
            return "redirect:/danh-sach-chien-dich";
        }
        model.addAttribute("campaign", campaign);
        return "ungho/campaign-detail";
    }

    @PostMapping("/ung-ho")
    public String donateMoney(@RequestParam("campaignId") Integer campaignId,
                              @RequestParam("amount") Double amount) {
        Campaign campaign = campaignService.getCampaignById(campaignId);
        if (campaign != null) {
            campaign.setCurrentAmount(campaign.getCurrentAmount() + amount);
            int currentCount = campaign.getDonationCount() != null ? campaign.getDonationCount() : 0;
            campaign.setDonationCount(currentCount + 1);
            campaignService.saveCampaign(campaign);
        }
        return "redirect:/chien-dich/" + campaignId;
    }

    // =========================================================================
    // API LẤY DANH SÁCH NGƯỜI ỦNG HỘ CHO TRANG CHI TIẾT
    // (Đã xóa API /stats vì nó đã tồn tại an toàn bên DonationController)
    // =========================================================================

    @GetMapping("/api/campaign/{id}/donations")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDonationList(@PathVariable Integer id) {
        // Lấy danh sách giao dịch đã thanh toán thành công
        List<Donation> list = donationRepository.findByCampaignIdAndStatusOrderByDonationTimeDesc(id, "PAID");

        // Format lại dữ liệu trước khi gửi về JS
        List<Map<String, Object>> responseList = list.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();

            // Xử lý ẩn danh
            boolean isAnon = (d.getIsAnonymous() != null && d.getIsAnonymous());
            map.put("fullName", isAnon ? "Nhà Hảo Tâm Ẩn Danh" : d.getFullName());
            map.put("amount", d.getAmount());

            // Format ngày tháng (Ví dụ: 02/04/2026 12:49)
            String formattedDate = "";
            if(d.getDonationTime() != null) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                formattedDate = d.getDonationTime().format(formatter);
            }
            map.put("createdAt", formattedDate);

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
    // Thêm API này để phục vụ việc nhảy số tự động ở trang DANH SÁCH
    @GetMapping("/api/campaigns/stats")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getMultipleCampaignStats(@RequestParam List<Integer> ids) {
        // Duyệt qua danh sách ID gửi lên từ Javascript
        List<Map<String, Object>> statsList = ids.stream().map(id -> {
            Campaign c = campaignService.getCampaignById(id);
            Map<String, Object> map = new HashMap<>();
            if (c != null) {
                map.put("id", c.getId());
                map.put("currentAmount", c.getCurrentAmount() != null ? c.getCurrentAmount() : 0);
                map.put("progressPercentage", c.getProgressPercentage());
                map.put("donationCount", c.getDonationCount() != null ? c.getDonationCount() : 0);
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(statsList);
    }
}