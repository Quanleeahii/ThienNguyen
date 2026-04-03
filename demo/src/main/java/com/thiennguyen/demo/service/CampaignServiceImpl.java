package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.Campaign;
import com.thiennguyen.demo.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;
    @Override
    public Page<Campaign> getFilteredCampaigns(String keyword, Integer categoryId, String status, String type, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (status != null && status.trim().isEmpty()) status = null;
        if (type != null && type.trim().isEmpty()) type = null;
        return campaignRepository.filterCampaigns(keyword, categoryId, status, type, pageable);
    }
    @Override
    public Campaign getCampaignById(Integer id) {
        return campaignRepository.findById(id).orElse(null);
    }
    @Override
    public Campaign saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }
    @Override
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }
    @Override
    public Map<Integer, Long> getCategoryCounts(String keyword, String status, String type) {
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (status != null && status.trim().isEmpty()) status = null;
        if (type != null && type.trim().isEmpty()) type = null;
        List<Object[]> results = campaignRepository.countCampaignsGroupByCategory(keyword, status, type);
        Map<Integer, Long> categoryCounts = new HashMap<>();
        for (Object[] result : results) {
            Integer catId = (Integer) result[0];
            Long count = (Long) result[1];
            categoryCounts.put(catId, count);
        }
        return categoryCounts;
    }
    @Override
    @Transactional
    public void plusAmount(Integer campaignId, Double amount) {
        // 1. Tìm chiến dịch trong DB
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch"));

        // 2. Cộng số tiền mới vào tổng tiền hiện tại
        Double currentAmount = campaign.getCurrentAmount() != null ? campaign.getCurrentAmount() : 0.0;
        campaign.setCurrentAmount(currentAmount + amount);

        // 3. Tăng số lượt người ủng hộ lên 1
        Integer currentCount = campaign.getDonationCount() != null ? campaign.getDonationCount() : 0;
        campaign.setDonationCount(currentCount + 1);

        // 4. Lưu lại vào Database
        campaignRepository.save(campaign);
    }
}