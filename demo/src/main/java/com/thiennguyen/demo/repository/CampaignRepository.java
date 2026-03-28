package com.thiennguyen.demo.repository;

import com.thiennguyen.demo.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    @Query("SELECT c FROM Campaign c WHERE " +
            "(:keyword IS NULL OR c.title LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:status IS NULL OR " +
            "   (:status = 'active' AND c.status = 'active' AND (c.endDate IS NULL OR c.endDate >= CURRENT_TIMESTAMP)) OR " +
            "   (:status = 'ended'  AND (c.status = 'ended' OR (c.status = 'active' AND c.endDate < CURRENT_TIMESTAMP))) OR " +
            "   (:status = 'goal'   AND c.status = 'goal') OR " +
            "   (:status = 'paused' AND c.status = 'paused')" +
            ") " +
            "ORDER BY " +
            "  CASE " +
            "    WHEN c.status IN ('ended', 'paused') THEN 1 " +
            "    WHEN c.endDate IS NOT NULL AND c.endDate < CURRENT_TIMESTAMP THEN 1 " +
            "    ELSE 0 " +
            "  END ASC, " +
            "  c.startDate DESC")
    Page<Campaign> filterCampaigns(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("status") String status,
            @Param("type") String type,
            Pageable pageable
    );
    @Query("SELECT c.category.id, COUNT(c) FROM Campaign c WHERE " +
            "(:keyword IS NULL OR c.title LIKE %:keyword%) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:status IS NULL OR " +
            "   (:status = 'active' AND c.status = 'active' AND (c.endDate IS NULL OR c.endDate >= CURRENT_TIMESTAMP)) OR " +
            "   (:status = 'ended'  AND (c.status = 'ended' OR (c.status = 'active' AND c.endDate < CURRENT_TIMESTAMP))) OR " +
            "   (:status = 'goal'   AND c.status = 'goal') OR " +
            "   (:status = 'paused' AND c.status = 'paused')" +
            ") " +
            "GROUP BY c.category.id")
    List<Object[]> countCampaignsGroupByCategory(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("type") String type
    );
}