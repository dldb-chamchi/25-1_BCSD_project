package com.example.dto.response;

import com.example.model.PostComment;
import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long memberId,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponseDto fromEntity(PostComment c) {
        return new CommentResponseDto(
                c.getId(),
                c.getMemberId(),
                c.getContent(),
                c.getCreatedAt()
        );
    }
}
