package com.example.goyimanagementbackend.enity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "VideoWatchHistory", uniqueConstraints = @UniqueConstraint(columnNames = {"videoId", "userId"}))
@Getter
@Setter
public class VideoWatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoId", nullable = false)
    private Video video;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false)
    private Integer watchProgress;

    private LocalDateTime lastWatchedAt;

    @PrePersist
    protected void onCreate() {
        lastWatchedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastWatchedAt = LocalDateTime.now();
    }
}