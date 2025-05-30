package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.CommentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentAttachmentRepository extends JpaRepository<CommentAttachment, Long> {
    // Tìm tệp đính kèm cho một bình luận cụ thể
    List<CommentAttachment> findByCommentId(Long commentId);
}