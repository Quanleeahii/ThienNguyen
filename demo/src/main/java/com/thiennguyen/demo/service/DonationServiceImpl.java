package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.entity.Donation;
import com.thiennguyen.demo.repository.CampaignRepository;
import com.thiennguyen.demo.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private PayOS payOS;

    @Override
    public Donation getDonationById(Integer id) {
        return donationRepository.findById(id).orElse(null);
    }

    @Override
    public Donation createDonation(Donation donation, Integer campaignId) {
        prepareDonation(donation, campaignId);
        return donationRepository.save(donation);
    }

    @Override
    public Donation getDonationByTransactionCode(String transactionCode) {
        return donationRepository.findByTransactionCode(transactionCode).orElse(null);
    }

    @Override
    @Transactional
    public Map<String, Object> createDonationWithPayOS(Donation donation, Integer campaignId) throws Exception {
        prepareDonation(donation, campaignId);

        long uniqueOrderCode = System.currentTimeMillis();
        donation.setTransactionCode(String.valueOf(uniqueOrderCode));

        Donation savedDonation = donationRepository.save(donation);

        long amount = donation.getAmount().longValue();

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(uniqueOrderCode)
                .amount(amount)
                .description(String.valueOf(uniqueOrderCode))
                .returnUrl("http://localhost:8080/chien-dich/" + campaignId)
                .cancelUrl("http://localhost:8080/chien-dich/" + campaignId)
                .build();

        CreatePaymentLinkResponse payosResponse = payOS.paymentRequests().create(paymentData);

        Map<String, Object> result = new HashMap<>();
        result.put("donationId", savedDonation.getId());
        result.put("checkoutUrl", payosResponse.getCheckoutUrl());
        result.put("qrCode", payosResponse.getQrCode());
        result.put("payosAccountNumber", payosResponse.getAccountNumber()); // Lấy STK ảo (V3CAS...)
        result.put("payosDescription", payosResponse.getDescription());     // Lấy nội dung chuẩn (CS25...)
        result.put("payosAccountName", payosResponse.getAccountName());     // Tên chủ TK
        result.put("payosBin", payosResponse.getBin());

        return result;
    }

    private void prepareDonation(Donation donation, Integer campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch"));

        donation.setCampaign(campaign);
        donation.setDonationTime(LocalDateTime.now());
        donation.setStatus("PENDING");

        if (donation.getIsAnonymous() == null) {
            donation.setIsAnonymous(false);
        }
    }
}