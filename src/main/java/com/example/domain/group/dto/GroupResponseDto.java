package com.example.domain.group.dto;

import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupStatus;

import java.time.LocalDateTime;

public record GroupResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime expiresAt,
        Integer maxMember,
        GroupStatus status,
        Integer participantCount
) {
    public static GroupResponseDto fromEntity(Group g) {
        return new GroupResponseDto(
                g.getId(),
                g.getTitle(),
                g.getDescription(),
                g.getExpiresAt(),
                g.getMaxMember(),
                g.getStatus(),
                g.getParticipants().size()
        );
    }
}
