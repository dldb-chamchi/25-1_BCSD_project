package com.example.dto.response;

import com.example.model.GroupPost;
import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long hostId,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static PostResponseDto fromEntity(GroupPost p) {
        return new PostResponseDto(
                p.getId(),
                p.getHostId(),
                p.getTitle(),
                p.getContent(),
                p.getCreatedAt()
        );
    }
}
