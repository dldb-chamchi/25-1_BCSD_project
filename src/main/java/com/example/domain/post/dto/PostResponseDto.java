package com.example.domain.post.dto;

import com.example.domain.post.model.Post;
import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long hostId,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static PostResponseDto fromEntity(Post p) {
        return new PostResponseDto(
                p.getId(),
                p.getHostId(),
                p.getTitle(),
                p.getContent(),
                p.getCreatedAt()
        );
    }
}
