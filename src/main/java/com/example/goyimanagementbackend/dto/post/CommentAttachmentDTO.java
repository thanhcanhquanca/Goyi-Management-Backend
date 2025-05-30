package com.example.goyimanagementbackend.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

// DTO cho tệp đính kèm trong bình luận
@Data
public class CommentAttachmentDTO {
    private Long id; // Mã định danh duy nhất của tệp đính kèm
    private String attachmentType; // Loại tệp đính kèm (IMAGE, FILE)
    private String attachmentUrl; // URL đến tệp đính kèm
    private String fileName; // Tên tệp (tùy chọn)
    private Integer fileSize; // Kích thước tệp tính bằng byte (tùy chọn)
    private String caption; // Chú thích cho tệp đính kèm (tùy chọn)
    private LocalDateTime createdAt; // Thời điểm tệp đính kèm được tải lên
}