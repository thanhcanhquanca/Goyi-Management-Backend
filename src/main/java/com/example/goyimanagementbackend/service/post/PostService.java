package com.example.goyimanagementbackend.service.post;

import com.example.goyimanagementbackend.dto.post.*;
import com.example.goyimanagementbackend.entity.*;
import com.example.goyimanagementbackend.repository.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostReactionRepository postReactionRepository;

    @Autowired
    private PostViewRepository postViewRepository;

    @Autowired
    private PostShareRepository postShareRepository;

    @Autowired
    private CommentAttachmentRepository commentAttachmentRepository;

    @Autowired
    private CommentMentionRepository commentMentionRepository;

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getAllPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("VIEW_POST"))) {
            throw new RuntimeException("Không có quyền xem bài viết");
        }

        List<Post> posts = postRepository.findAllByStatusNot(Post.PostStatus.DELETED);
        return posts.stream()
                .map(this::mapToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPostDetails(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("VIEW_POST"))) {
            throw new RuntimeException("Không có quyền xem bài viết");
        }

        Post post = postRepository.findByIdAndStatusNot(postId, Post.PostStatus.DELETED)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết hoặc bài viết đã bị xóa"));
        return mapToPostResponseDTO(post);
    }

    private PostResponseDTO mapToPostResponseDTO(Post post) {
        PostResponseDTO response = new PostResponseDTO();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setStatus(post.getStatus().name());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        UserPostDTO userDTO = new UserPostDTO();
        userDTO.setUserId(post.getUser().getUserId());
        userDTO.setUserName(post.getUser().getUserName());
        userDTO.setProfilePicture(post.getUser().getProfilePicture());
        response.setUser(userDTO);

        List<PostImageDTO> images = postImageRepository.findByPostId(post.getId()).stream()
                .map(this::mapToPostImageDTO)
                .collect(Collectors.toList());
        response.setImages(images);

        List<PostCommentDTO> comments = postCommentRepository
                .findByPostIdAndParentCommentIsNullAndStatusNot(post.getId(), PostComment.Status.DELETED)
                .stream()
                .map(comment -> mapToPostCommentDTO(comment, post.getId()))
                .collect(Collectors.toList());
        response.setComments(comments);

        List<PostReactionDTO> reactions = postReactionRepository.findByPostId(post.getId()).stream()
                .map(this::mapToPostReactionDTO)
                .collect(Collectors.toList());
        response.setReactions(reactions);

        response.setViewCount(postViewRepository.findByPostId(post.getId())
                .map(PostView::getViewCount)
                .orElse(0));

        List<PostShareDTO> shares = postShareRepository.findByPostId(post.getId()).stream()
                .map(this::mapToPostShareDTO)
                .collect(Collectors.toList());
        response.setShares(shares);

        return response;
    }

    private PostImageDTO mapToPostImageDTO(PostImage image) {
        PostImageDTO dto = new PostImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType().name());
        dto.setCaption(image.getCaption());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }

    private PostCommentDTO mapToPostCommentDTO(PostComment comment, Long postId) {
        PostCommentDTO dto = new PostCommentDTO();
        dto.setId(comment.getId());
        dto.setCommentText(comment.getCommentText());
        dto.setPosterResponse(comment.isPosterResponse());
        dto.setStatus(comment.getStatus().name());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        UserPostDTO userDTO = new UserPostDTO();
        userDTO.setUserId(comment.getUser().getUserId());
        userDTO.setUserName(comment.getUser().getUserName());
        userDTO.setProfilePicture(comment.getUser().getProfilePicture());
        dto.setUser(userDTO);

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        List<PostCommentDTO> childComments = postCommentRepository
                .findByPostIdAndParentCommentIsNullAndStatusNot(postId, PostComment.Status.DELETED)
                .stream()
                .filter(c -> c.getParentComment() != null && c.getParentComment().getId().equals(comment.getId()))
                .map(c -> mapToPostCommentDTO(c, postId))
                .collect(Collectors.toList());
        dto.setChildComments(childComments);

        List<CommentAttachmentDTO> attachments = commentAttachmentRepository.findByCommentId(comment.getId()).stream()
                .map(this::mapToCommentAttachmentDTO)
                .collect(Collectors.toList());
        dto.setAttachments(attachments);

        List<CommentMentionDTO> mentions = commentMentionRepository.findByCommentId(comment.getId()).stream()
                .map(this::mapToCommentMentionDTO)
                .collect(Collectors.toList());
        dto.setMentions(mentions);

        return dto;
    }

    private CommentAttachmentDTO mapToCommentAttachmentDTO(CommentAttachment attachment) {
        CommentAttachmentDTO dto = new CommentAttachmentDTO();
        dto.setId(attachment.getId());
        dto.setAttachmentType(attachment.getAttachmentType().name());
        dto.setAttachmentUrl(attachment.getAttachmentUrl());
        dto.setFileName(attachment.getFileName());
        dto.setFileSize(attachment.getFileSize());
        dto.setCaption(attachment.getCaption());
        dto.setCreatedAt(attachment.getCreatedAt());
        return dto;
    }

    private CommentMentionDTO mapToCommentMentionDTO(CommentMention mention) {
        CommentMentionDTO dto = new CommentMentionDTO();
        dto.setId(mention.getId());
        dto.setNotified(mention.isNotified());
        dto.setCreatedAt(mention.getCreatedAt());

        UserPostDTO userDTO = new UserPostDTO();
        userDTO.setUserId(mention.getMentionedUser().getUserId());
        userDTO.setUserName(mention.getMentionedUser().getUserName());
        userDTO.setProfilePicture(mention.getMentionedUser().getProfilePicture());
        dto.setMentionedUser(userDTO);

        return dto;
    }

    private PostReactionDTO mapToPostReactionDTO(PostReaction reaction) {
        PostReactionDTO dto = new PostReactionDTO();
        dto.setId(reaction.getId());
        dto.setReactionType(reaction.getReactionType().name());
        dto.setCreatedAt(reaction.getCreatedAt());
        dto.setUpdatedAt(reaction.getUpdatedAt());

        UserPostDTO userDTO = new UserPostDTO();
        userDTO.setUserId(reaction.getUser().getUserId());
        userDTO.setUserName(reaction.getUser().getUserName());
        userDTO.setProfilePicture(reaction.getUser().getProfilePicture());
        dto.setUser(userDTO);

        return dto;
    }

    private PostShareDTO mapToPostShareDTO(PostShare share) {
        PostShareDTO dto = new PostShareDTO();
        dto.setId(share.getId());
        dto.setSharePlatform(share.getSharePlatform().name());
        dto.setCreatedAt(share.getCreatedAt());

        UserPostDTO userDTO = new UserPostDTO();
        userDTO.setUserId(share.getUser().getUserId());
        userDTO.setUserName(share.getUser().getUserName());
        userDTO.setProfilePicture(share.getUser().getProfilePicture());
        dto.setUser(userDTO);

        return dto;
    }
}