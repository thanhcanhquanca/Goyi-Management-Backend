package com.example.goyimanagementbackend.enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    private User sender;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @NotNull
    @Column(nullable = false)
    private Integer referenceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferenceType referenceType;

    @NotNull
    @Column(nullable = false)
    private String message;

    private boolean isRead;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        MENTION, LIKE, COMMENT, SHARE, NEW_POST, NEW_VIDEO, VIDEO_COMMENT, POST_COMMENT, POST_REPLY, VIDEO_REPLY
    }

    public enum ReferenceType {
        POST, VIDEO, COMMENT
    }
}