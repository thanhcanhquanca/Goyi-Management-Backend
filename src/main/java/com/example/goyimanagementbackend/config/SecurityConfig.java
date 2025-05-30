package com.example.goyimanagementbackend.config;

import com.example.goyimanagementbackend.security.CustomUserDetailsService;
import com.example.goyimanagementbackend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Thêm cấu hình CORS
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/update-profile").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyAuthority(
                                "DELETE_POST_VIOLATION", "DELETE_VIDEO_VIOLATION",
                                "DELETE_COMMENT_VIOLATION", "DELETE_VIDEO_COMMENT_VIOLATION")
                        .requestMatchers("/api/posts/**").hasAnyAuthority(
                                "CREATE_POST", "UPDATE_POST", "DELETE_POST", "VIEW_POST")
                        .requestMatchers("/api/videos/**").hasAnyAuthority(
                                "CREATE_VIDEO", "UPDATE_VIDEO", "DELETE_VIDEO", "VIEW_VIDEO")
                        .requestMatchers("/api/comments/**").hasAnyAuthority(
                                "CREATE_COMMENT", "UPDATE_COMMENT", "DELETE_COMMENT", "VIEW_COMMENT")
                        .requestMatchers("/api/video-comments/**").hasAnyAuthority(
                                "CREATE_VIDEO_COMMENT", "UPDATE_VIDEO_COMMENT",
                                "DELETE_VIDEO_COMMENT", "VIEW_VIDEO_COMMENT")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Thay bằng domain của frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả đường dẫn
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}