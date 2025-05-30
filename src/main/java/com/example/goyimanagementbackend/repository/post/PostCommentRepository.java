package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    // Tìm bình luận cha cho một bài viết, loại trừ bình luận đã bị xóa
    List<PostComment> findByPostIdAndParentCommentIsNullAndStatusNot(Long postId, PostComment.Status status);
}