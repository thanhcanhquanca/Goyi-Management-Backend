package com.example.goyimanagementbackend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AdCampaigns")
@Getter
@Setter
public class AdCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetId", nullable = false)
    private AdAsset asset;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignType campaignType = CampaignType.USER;

    @NotNull
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType budgetType;

    @Column(precision = 12, scale = 4)
    private BigDecimal budgetAmount;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ContentType {
        VIDEO, IMAGE
    }

    public enum CampaignType {
        USER, ADMIN
    }

    public enum Status {
        PENDING, ACTIVE, PAUSED, COMPLETED, REJECTED
    }

    public enum BudgetType {
        DAILY, WEEKLY, MONTHLY, TOTAL
    }
}