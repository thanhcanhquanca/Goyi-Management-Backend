package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {
    // Tìm các nhắc đến cho một bình luận cụ thể
    List<CommentMention> findByCommentId(Long commentId);
}