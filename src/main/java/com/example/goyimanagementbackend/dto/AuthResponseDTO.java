package com.example.goyimanagementbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private String userName;
    private String role;
    private List<String> permissions;
    private String profilePicture;
    private String userCode;
}