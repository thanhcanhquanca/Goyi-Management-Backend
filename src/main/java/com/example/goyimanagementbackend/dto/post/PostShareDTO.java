package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

// DTO cho chia sẻ bài viết
@Data
public class PostShareDTO {
    private Long id; // Mã định danh duy nhất của chia sẻ
    private String sharePlatform; // Nền tảng nơi bài viết được chia sẻ (FACEBOOK, TWITTER, v.v.)
    private UserPostDTO user; // Người dùng đã chia sẻ bài viết
    private LocalDateTime createdAt; // Thời điểm bài viết được chia sẻ
}