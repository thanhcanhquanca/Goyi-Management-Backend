package com.example.goyimanagementbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**___ Configuration đánh dấu đây là một cấu hình của file Spring ___*/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**___ Đường dẫn thư mục file , lấy từ file cấu hình ___*/
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**___ Định nghĩa cách xử lý các yêu cầu HTTP đến các tài nguyên tĩnh (như hình ảnh). ___*/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}