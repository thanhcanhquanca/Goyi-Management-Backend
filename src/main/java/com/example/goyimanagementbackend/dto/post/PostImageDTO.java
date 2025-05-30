package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

// DTO cho hình ảnh bài viết
@Data
public class PostImageDTO {
    private Long id; // Mã định danh duy nhất của hình ảnh
    private String imageUrl; // URL đến hình ảnh
    private String imageType; // Loại hình ảnh (AI_GENERATED, USER_UPLOADED, v.v.)
    private String caption; // Chú thích cho hình ảnh (tùy chọn)
    private LocalDateTime createdAt; // Thời điểm hình ảnh được tải lên
}