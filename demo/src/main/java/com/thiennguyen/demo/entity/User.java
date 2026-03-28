package com.thiennguyen.demo.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String role;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @Nationalized
    @Column(name = "full_name")
    private String fullName;
    private String phone;
    private String gender;
    private String avatar;
    @Nationalized
    private String address;
    @Column(columnDefinition = "TEXT")
    private String bio;
    @Column(name = "link_fb")
    private String linkFb;
    @Column(name = "link_yt")
    private String linkYt;
    @Column(name = "link_tiktok")
    private String linkTiktok;
    @Column(name = "contact_email")
    private String contactEmail;
    @Column(name = "social_link")
    private String socialLink;
    private LocalDate dob;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Donation> donations;
}