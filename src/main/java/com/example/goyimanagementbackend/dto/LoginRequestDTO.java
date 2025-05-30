package com.example.goyimanagementbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{4,15}$", message = "Số điện thoại phải từ 9 đến 15 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}