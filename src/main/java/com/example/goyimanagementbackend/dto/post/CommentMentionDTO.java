package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

// DTO cho các nhắc đến trong bình luận
@Data
public class CommentMentionDTO {
    private Long id; // Mã định danh duy nhất của nhắc đến
    private UserPostDTO mentionedUser; // Người dùng được nhắc đến
    private boolean isNotified; // Cho biết người dùng được nhắc đến đã được thông báo chưa
    private LocalDateTime createdAt; // Thời điểm nhắc đến được tạo
}