package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {
    // Tìm số lượt xem cho một bài viết cụ thể
    Optional<PostView> findByPostId(Long postId);
}