package com.example.goyimanagementbackend.enity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AdImpressions", indexes = @Index(name = "idx_isPaid", columnList = "isPaid"))
@Getter
@Setter
public class AdImpression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignId", nullable = false)
    private AdCampaign campaign;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placementId", nullable = false)
    private AdPlacement placement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoId")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImpressionType impressionType;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal revenue = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal cost = BigDecimal.ZERO;

    private boolean isPaid;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ImpressionType {
        VIEW, CLICK
    }
}