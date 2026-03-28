package com.thiennguyen.demo.service;

import com.thiennguyen.demo.entity.Donation;

public interface DonationService {
    Donation createDonation(Donation donation, Integer campaignId);
    Donation getDonationByTransactionCode(String transactionCode);
}