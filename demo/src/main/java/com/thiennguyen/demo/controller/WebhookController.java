package com.thiennguyen.demo.controller;

import com.thiennguyen.demo.dto.PayOSWebhookRequest;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.repository.DonationRepository;
import com.thiennguyen.demo.service.CampaignService;
import com.thiennguyen.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/payos")
    public ResponseEntity<String> receivePayOSWebhook(@RequestBody PayOSWebhookRequest request) {
        try {
            if ("00".equals(request.getCode()) && request.getData() != null) {

                String transactionCode = String.valueOf(request.getData().getOrderCode());
                Donation donation = donationRepository.findByTransactionCode(transactionCode).orElse(null);

                if (donation != null && "PENDING".equals(donation.getStatus())) {
                    donation.setStatus("PAID");
                    donationRepository.save(donation);

                    campaignService.plusAmount(donation.getCampaign().getId(), donation.getAmount());

                    if (donation.getEmail() != null && !donation.getEmail().trim().isEmpty()) {
                        String donorName = donation.getFullName();
                        if (donation.getIsAnonymous() != null && donation.getIsAnonymous()) {
                            donorName = "Nhà Hảo Tâm Ẩn Danh";
                        }

                        emailService.sendThankYouEmail(
                                donation.getEmail(),
                                donorName,
                                donation.getAmount(),
                                donation.getCampaign().getTitle()
                        );
                    }

                    System.out.println("=========================================");
                    System.out.println("✅ TING TING! VỪA NHẬN ĐƯỢC TIỀN:");
                    System.out.println("💵 Số tiền: " + donation.getAmount() + " VNĐ");
                    System.out.println("📝 Cho chiến dịch ID: " + donation.getCampaign().getId());
                    System.out.println("=========================================");
                }
            }
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error");
        }
    }
}