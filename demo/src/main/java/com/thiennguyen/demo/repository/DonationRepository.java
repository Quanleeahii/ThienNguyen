package com.thiennguyen.demo.repository;

import com.thiennguyen.demo.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {

    Optional<Donation> findByTransactionCode(String transactionCode);

    List<Donation> findByCampaignIdAndStatusOrderByDonationTimeDesc(Integer campaignId, String status);
    List<Donation> findByCampaignIdOrderByDonationTimeDesc(Integer campaignId);
    // --- MỚI THÊM CHO ADMIN ---

    // 1. Lấy tất cả giao dịch mới nhất để làm "Sổ cái"
    List<Donation> findAllByOrderByDonationTimeDesc();

    // 2. Tính tổng số tiền quyên góp được (Chỉ tính những đơn đã PAID)
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.status = 'PAID'")
    Double sumTotalAmount();

    // 3. Đếm tổng số lượt ủng hộ thành công
    long countByStatus(String status);
}