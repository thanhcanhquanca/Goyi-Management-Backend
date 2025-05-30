package com.example.goyimanagementbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
    @Column(nullable = false)
    private String userName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{4,15}$", message = "Số điện thoại phải từ 9 đến 15 chữ số")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Email(message = "Email không hợp lệ")
    @Column(unique = true, nullable = true)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

    @NotNull(message = "Trạng thái không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    private String address;

    private String profilePicture;

    private String coverPhoto;

    @Column(unique = true)
    private String userCode;

    private boolean is2faEnabled;

    private String twoFASecret;

    private int copyrightViolations;

    private int warningCount;

    @Column(length = 1000)
    private String qrCode; // Thêm trường để lưu đường dẫn ảnh QR

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, LOCKED, SUSPENDED
    }
}