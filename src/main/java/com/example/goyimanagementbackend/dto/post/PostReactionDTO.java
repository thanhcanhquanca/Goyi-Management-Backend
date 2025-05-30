package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

// DTO cho phản ứng bài viết
@Data
public class PostReactionDTO {
    private Long id; // Mã định danh duy nhất của phản ứng
    private String reactionType; // Loại phản ứng (LIKE, HEART, LAUGH, ANGRY)
    private UserPostDTO user; // Người dùng đã phản ứng
    private LocalDateTime createdAt; // Thời điểm phản ứng được tạo
    private LocalDateTime updatedAt; // Thời điểm phản ứng được cập nhật lần cuối
}