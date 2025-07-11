package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.model.PostComment;
import com.example.model.GroupPost;

public record CommentRequestDto(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다")
        @Size(max = 500, message = "댓글 내용은 500자 이하여야 합니다")
        String content
) {
    public PostComment toEntity(GroupPost post, Long memberId) {
        return PostComment.builder()
                .post(post)
                .memberId(memberId)
                .content(content)
                .build();
    }
}
