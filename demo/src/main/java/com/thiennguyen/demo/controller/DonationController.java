package com.thiennguyen.demo.controller;
import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
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
        if (campaign == null) {
            return "redirect:/danh-sach-chien-dich";
        }
        model.addAttribute("campaign", campaign);
        model.addAttribute("donation", new Donation());
        return "ungho/donation";
    }
    @PostMapping("/donate")
    public String processDonation(@ModelAttribute("donation") Donation donation,
                                  @RequestParam("campaignId") Integer campaignId) {
        Donation savedDonation = donationService.createDonation(donation, campaignId);
        return "redirect:/donate/qr?transactionCode=" + savedDonation.getTransactionCode();
    }
    @GetMapping("/donate/qr")
    public String showQRCode(@RequestParam("transactionCode") String transactionCode, Model model) {
        Donation donation = donationService.getDonationByTransactionCode(transactionCode);
        if (donation == null) {
            return "redirect:/danh-sach-chien-dich";
        }
        model.addAttribute("donation", donation);
        return "donate-qr";
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
        Donation donation = new Donation();
        donation.setAmount(amount);
        donation.setMessage(message);
        donation.setFullName(fullName);
        donation.setEmail(email);
        donation.setIsAnonymous(isAnonymous);
        Donation savedDonation = donationService.createDonation(donation, campaignId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("transactionCode", savedDonation.getTransactionCode());
        return ResponseEntity.ok(response);
    }
}