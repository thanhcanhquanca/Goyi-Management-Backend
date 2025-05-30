package com.example.goyimanagementbackend.controller;

import com.example.goyimanagementbackend.dto.LoginRequestDTO;
import com.example.goyimanagementbackend.dto.RegisterRequestDTO;
import com.example.goyimanagementbackend.dto.UpdateUserProfileDTO;
import com.example.goyimanagementbackend.entity.User;
import com.example.goyimanagementbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**___ RequestMapping định nghĩa base URL cho các endpoint ___*/
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
//@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage(),
                            (existing, replacement) -> existing, HashMap::new
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.ok(userService.login(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage(),
                            (existing, replacement) -> existing, HashMap::new
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            User user = userService.register(registerRequest);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công",
                    "userName", user.getUserName(),
                    "userCode", user.getUserCode(),
                    "phoneNumber", user.getPhoneNumber(),
                    "qrCode", user.getQrCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Đăng ký thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String phoneNumber = authentication.getName();
            try {
                User user = userService.findUserByPhoneNumber(phoneNumber);
                if (user != null) {
                    user.setLastLogin(LocalDateTime.now());
                    userService.saveUser(user);
                    return ResponseEntity.ok(Map.of(
                            "message", "Đăng xuất thành công",
                            "userName", user.getUserName(),
                            "userId", user.getUserId(),
                            "phoneNumber", user.getPhoneNumber()
                    ));
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Đăng xuất thất bại: " + e.getMessage()));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin người dùng"));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @ModelAttribute UpdateUserProfileDTO updateRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage(),
                            (existing, replacement) -> existing, HashMap::new
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Lấy phoneNumber từ JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phoneNumber = authentication.getName();

            User updatedUser = userService.updateUserProfile(phoneNumber, updateRequest);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật thông tin thành công",
                    "userName", updatedUser.getUserName(),
                    "userCode", updatedUser.getUserCode(),
                    "email", updatedUser.getEmail() != null ? updatedUser.getEmail() : "",
                    "profilePicture", updatedUser.getProfilePicture() != null ? updatedUser.getProfilePicture() : "",
                    "coverPhoto", updatedUser.getCoverPhoto() != null ? updatedUser.getCoverPhoto() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cập nhật thất bại: " + e.getMessage()));
        }
    }
}
