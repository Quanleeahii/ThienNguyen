package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.Donation;
import java.util.Map;

public interface DonationService {
    Donation createDonation(Donation donation, Integer campaignId);
    Donation getDonationByTransactionCode(String transactionCode);
    Donation getDonationById(Integer id);
    Map<String, Object> createDonationWithPayOS(Donation donation, Integer campaignId) throws Exception;
}