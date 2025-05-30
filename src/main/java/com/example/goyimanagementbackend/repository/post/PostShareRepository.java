package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    // Tìm các lượt chia sẻ cho một bài viết cụ thể
    List<PostShare> findByPostId(Long postId);
}