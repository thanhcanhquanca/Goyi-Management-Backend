package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // Tìm hình ảnh cho một bài viết cụ thể
    List<PostImage> findByPostId(Long postId);
}