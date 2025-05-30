package com.example.goyimanagementbackend.security;

import com.example.goyimanagementbackend.entity.Permission;
import com.example.goyimanagementbackend.entity.User;
import com.example.goyimanagementbackend.repository.PermissionRepository;
import com.example.goyimanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**___ dùng truy vấn người dùng ___*/
    @Autowired
    private UserRepository userRepository;

    /**___ dùng truy vấn quyền ___*/
    @Autowired
    private PermissionRepository permissionRepository;

    /**___ chỉ đọc tối ưu & Lấy danh sách quyền được phép &  Chuyển quyền thành danh sách authorities___*/
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));

        List<String> permissions = permissionRepository.findByRoleRoleIdAndIsAllowedTrue(user.getRole().getRoleId())
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toList());


        List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = permissions.stream()
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                authorities
        );  // Trả về UserDetails cho Spring Security
    }
}