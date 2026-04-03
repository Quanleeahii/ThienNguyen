package com.thiennguyen.demo.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
@Entity
@Table(name = "donations")
@Data
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double amount;
    @Nationalized
    private String message;
    @Nationalized
    @Column(name = "full_name")
    private String fullName;
    private String email;
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;
    @Column(name = "donation_time")
    private LocalDateTime donationTime;
    private String status;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "transaction_code")
    private String transactionCode;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}