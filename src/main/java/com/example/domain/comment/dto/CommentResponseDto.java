package com.example.domain.comment.dto;

import com.example.domain.comment.model.Comment;
import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long memberId,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponseDto fromEntity(Comment c) {
        return new CommentResponseDto(
                c.getId(),
                c.getMemberId(),
                c.getContent(),
                c.getCreatedAt()
        );
    }
}
