package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DonationController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DonationService donationService;

    @GetMapping("/donate")
    public String showDonateForm(@RequestParam("campaignId") Integer campaignId, Model model) {
        Campaign campaign = campaignService.getCampaignById(campaignId);

        if (campaign == null ||
                campaign.isClosed() ||
                "paused".equals(campaign.getStatus()) ||
                "ended".equals(campaign.getStatus())) {
            return "redirect:/chien-dich/" + campaignId;
        }

        model.addAttribute("campaign", campaign);
        return "ungho/donation";
    }

    @PostMapping("/api/donate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processDonationAjax(
            @RequestParam("campaignId") Integer campaignId,
            @RequestParam("amount") Double amount,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "isAnonymous", defaultValue = "false") Boolean isAnonymous) {

        try {
            Donation donation = new Donation();
            donation.setAmount(amount);
            donation.setMessage(message);
            donation.setFullName(fullName);
            donation.setEmail(email);
            donation.setIsAnonymous(isAnonymous);

            Map<String, Object> payosData = donationService.createDonationWithPayOS(donation, campaignId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("donationId", payosData.get("donationId"));
            response.put("qrCode", payosData.get("qrCode"));
            response.put("checkoutUrl", payosData.get("checkoutUrl"));
            response.put("accountNumber", payosData.get("payosAccountNumber"));
            response.put("description", payosData.get("payosDescription"));
            response.put("accountName", payosData.get("payosAccountName"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/api/donation/check-status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkDonationStatus(@PathVariable Integer id) {
        Donation d = donationService.getDonationById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("isPaid", d != null && "PAID".equals(d.getStatus()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/api/campaign/{id}/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCampaignStats(@PathVariable Integer id) {
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign == null) return ResponseEntity.notFound().build();

        Map<String, Object> stats = new HashMap<>();
        stats.put("currentAmount", campaign.getCurrentAmount() != null ? campaign.getCurrentAmount() : 0);
        stats.put("progressPercentage", campaign.getProgressPercentage());
        stats.put("donationCount", campaign.getDonationCount() != null ? campaign.getDonationCount() : 0);

        return ResponseEntity.ok(stats);
    }
}