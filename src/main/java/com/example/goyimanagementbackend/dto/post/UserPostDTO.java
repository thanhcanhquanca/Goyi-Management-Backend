package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

// DTO cho thông tin người dùng liên quan đến bài viết
@Data
public class UserPostDTO {
    private Long userId; // Mã định danh duy nhất của người dùng
    private String userName; // Tên người dùng
    private String profilePicture; // URL đến ảnh đại diện của người dùng
}