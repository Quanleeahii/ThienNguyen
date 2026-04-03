package com.thiennguyen.demo.service;
import com.thiennguyen.demo.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
public interface CampaignService {
    Page<Campaign> getFilteredCampaigns(String keyword, Integer categoryId, String status, String type, Pageable pageable);
    Campaign getCampaignById(Integer id);
    Campaign saveCampaign(Campaign campaign);
    List<Campaign> getAllCampaigns();
    Map<Integer, Long> getCategoryCounts(String keyword, String status, String type);
    // Hàm dùng để cộng tiền khi PayOS báo ting ting
    void plusAmount(Integer campaignId, Double amount);
}