package com.thiennguyen.demo.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import java.util.List;
@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Nationalized
    private String name;
    private String icon;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;
    @Transient
    public int getCampaignCount() {
        return campaigns != null ? campaigns.size() : 0;
    }
}