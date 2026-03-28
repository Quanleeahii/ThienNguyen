package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.repository.CampaignRepository;
import com.thiennguyen.demo.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class DonationServiceImpl implements DonationService {
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Override
    public Donation createDonation(Donation donation, Integer campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch"));
        donation.setCampaign(campaign);
        donation.setDonationTime(LocalDateTime.now());
        donation.setStatus("PENDING");
        String randomCode = "UH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        donation.setTransactionCode(randomCode);
        if (donation.getIsAnonymous() == null) {
            donation.setIsAnonymous(false);
        }
        return donationRepository.save(donation);
    }
    @Override
    public Donation getDonationByTransactionCode(String transactionCode) {
        return donationRepository.findByTransactionCode(transactionCode).orElse(null);
    }
}