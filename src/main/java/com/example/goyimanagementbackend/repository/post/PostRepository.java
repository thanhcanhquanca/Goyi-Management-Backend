package com.example.goyimanagementbackend.repository.post;

import com.example.goyimanagementbackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Tìm bài viết theo ID, loại trừ bài viết đã bị xóa
    Optional<Post> findByIdAndStatusNot(Long id, Post.PostStatus status);

    // Tìm tất cả bài viết chưa bị xóa
    List<Post> findAllByStatusNot(Post.PostStatus status);
}