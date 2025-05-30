package com.example.goyimanagementbackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil là lớp tiện ích dùng để quản lý và xử lý JWT (JSON Web Token)
 * trong các ứng dụng web để xác thực người dùng và phân quyền truy cập.
 * Component Đánh dấu class này là một thành phần Spring.
 */
@Component
public class JwtUtil {

    /**___ Khóa bí mật để ký và xác minh JWT, lấy từ file cấu hình ___*/
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    /**___ Thời gian sống của token (mili giây), lấy từ file cấu hình ___*/
    @Value("${app.jwt.expiration}")
    private long JWT_TOKEN_VALIDITY;

    /** ___ Trích xuất tên người dùng (phoneNumber) từ token ___*/
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** ___ Trích xuất thời gian hết hạn từ token ___*/
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** ___ Lấy toàn bộ claims từ token & Áp dụng hàm claimsResolver để lấy giá trị mong muốn ___*/
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** ___ Đặt khóa bí mật để xác minh & Phân tích token & Trả về claims ___*/
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /** ___ Kiểm tra token đã hết hạn hay chưa ___*/
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** ___ Tạo map để lưu claims (hiện tại để trống) & Tạo token với username (phoneNumber) ___*/
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /** ___ Đặt claims (hiện tại là rỗng)
     * & Đặt subject (phoneNumber)
     * & Đặt thời gian phát hành
     * & Đặt thời gian hết hạn
     * & Ký token bằng HS512 & Chuyển thành chuỗi token
     * ___*/
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Thời gian sống 1 tháng
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /** ___ Lấy username từ token & Kiểm tra username và thời gian ___*/
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}