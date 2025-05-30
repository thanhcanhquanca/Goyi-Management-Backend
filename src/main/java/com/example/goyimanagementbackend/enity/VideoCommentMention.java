package com.example.goyimanagementbackend.enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "VideoCommentMentions")
@Getter
@Setter
public class VideoCommentMention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", nullable = false)
    private VideoComment comment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentionedUserId", nullable = false)
    private User mentionedUser;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private boolean isNotified;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
