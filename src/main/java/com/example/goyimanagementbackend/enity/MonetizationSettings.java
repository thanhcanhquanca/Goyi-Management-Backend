package com.example.goyimanagementbackend.enity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MonetizationSettings", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "videoId", "channelId"}))
@Getter
@Setter
public class MonetizationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channelId")
    private User channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoId")
    private Video video;

    @NotNull
    @Column(nullable = false)
    private boolean isMonetized;

    @NotNull
    @Column(nullable = false)
    private int subscriptionCount;

    @NotNull
    @Column(nullable = false)
    private long totalViews;

    @NotNull
    @Column(nullable = false)
    private boolean isEligible;

    private LocalDateTime activatedAt;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

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
}