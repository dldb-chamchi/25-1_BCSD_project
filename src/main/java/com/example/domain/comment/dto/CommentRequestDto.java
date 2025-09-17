package com.example.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.domain.comment.model.Comment;
import com.example.domain.post.model.Post;

public record CommentRequestDto(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다")
        @Size(max = 500, message = "댓글 내용은 500자 이하여야 합니다")
        String content
) {
    public Comment toEntity(Post post, Long memberId) {
        return Comment.builder()
                .post(post)
                .memberId(memberId)
                .content(content)
                .build();
    }
}
