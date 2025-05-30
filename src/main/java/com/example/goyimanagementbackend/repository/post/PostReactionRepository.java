package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    // Tìm phản ứng cho một bài viết cụ thể
    List<PostReaction> findByPostId(Long postId);
}