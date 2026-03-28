package com.thiennguyen.demo.entity;
import org.hibernate.annotations.Nationalized;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit;
@Entity
@Table(name = "campaigns")
@Data
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Nationalized
    private String title;
    @Column(name = "image_url")
    private String imageUrl;
    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String storyContent;
    @Column(name = "target_amount")
    private Double targetAmount;
    @Column(name = "current_amount")
    private Double currentAmount = 0.0;
    @Column(name = "donation_count")
    private Integer donationCount;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Nationalized
    @Column(name = "location_name")
    private String locationName;
    private String type;
    private String status;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_account_number")
    private String bankAccountNumber;
    @Nationalized
    @Column(name = "bank_account_name")
    private String bankAccountName;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<Donation> donations;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Transient
    public int getProgressPercentage() {
        if (targetAmount == null || targetAmount == 0) {
            return 0;
        }
        double percentage = (currentAmount / targetAmount) * 100;
        return (int) Math.round(percentage);
    }
    @Transient
    public long getDaysRemaining() {
        if (endDate == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
        return days > 0 ? days : 0;
    }
    @PrePersist
    public void onPrePersist() {
        if (this.currentAmount == null) this.currentAmount = 0.0;
        if (this.donationCount == null) this.donationCount = 0;
        updateStatusAutomatically();
    }
    @PreUpdate
    public void onPreUpdate() {
        updateStatusAutomatically();
    }
    private void updateStatusAutomatically() {
        if ("paused".equals(this.status)) {
            return;
        }
        if (this.targetAmount != null && this.currentAmount >= this.targetAmount) {
            this.status = "goal";
        }
        else if (this.endDate != null && LocalDateTime.now().isAfter(this.endDate)) {
            this.status = "ended";
        }
        else {
            this.status = "active";
        }
    }
    @Transient
    public boolean isClosed() {
        // 1. Kiểm tra trạng thái cứng trong DB (Admin bấm Tạm dừng hoặc Đạt mục tiêu)
        if ("paused".equals(this.status) || "goal".equals(this.status)) {
            return true;
        }

        // 2. Kiểm tra Real-time: Nếu ngày hôm nay đã vượt quá ngày kết thúc
        if (this.endDate != null && LocalDateTime.now().isAfter(this.endDate)) {
            return true;
        }

        // Nếu qua hết các ải trên thì chiến dịch vẫn đang chạy ngon lành
        return false;
    }
}