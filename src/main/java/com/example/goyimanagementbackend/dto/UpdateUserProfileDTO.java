package com.example.goyimanagementbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserProfileDTO {
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
    private String userName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private MultipartFile profilePicture;

    private MultipartFile coverPhoto;
}