package com.thiennguyen.demo.controller;
import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
@Controller
public class CampaignController {
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private CategoryService categoryService;
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
}