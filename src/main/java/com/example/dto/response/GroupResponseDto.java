package com.example.dto.response;

import com.example.model.PurchaseGroup;
import java.time.LocalDateTime;

public record GroupResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime expiresAt,
        Integer maxMembers,
        String status,
        Integer participantCount
) {
    public static GroupResponseDto fromEntity(PurchaseGroup g) {
        return new GroupResponseDto(
                g.getId(),
                g.getTitle(),
                g.getDescription(),
                g.getExpiresAt(),
                g.getMaxMembers(),
                g.getStatus(),
                g.getParticipants().size()
        );
    }
}
