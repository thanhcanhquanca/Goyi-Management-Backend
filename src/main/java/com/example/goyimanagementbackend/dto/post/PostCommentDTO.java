package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// DTO cho bình luận bài viết (hỗ trợ cấu trúc bình luận cha-con)
@Data
public class PostCommentDTO {
    private Long id; // Mã định danh duy nhất của bình luận
    private String commentText; // Nội dung văn bản của bình luận
    private boolean isPosterResponse; // Cho biết bình luận có phải là phản hồi từ tác giả bài viết không
    private String status; // Trạng thái của bình luận (ACTIVE, DELETED, HIDDEN)
    private LocalDateTime createdAt; // Thời điểm bình luận được tạo
    private LocalDateTime updatedAt; // Thời điểm bình luận được cập nhật lần cuối
    private UserPostDTO user; // Người dùng đã viết bình luận
    private Long parentCommentId; // Mã của bình luận cha (null đối với bình luận cha)
    private List<PostCommentDTO> childComments; // Danh sách các bình luận con (cho các phản hồi lồng nhau)
    private List<CommentAttachmentDTO> attachments; // Danh sách các tệp đính kèm trong bình luận
    private List<CommentMentionDTO> mentions; // Danh sách người dùng được nhắc đến trong bình luận
}