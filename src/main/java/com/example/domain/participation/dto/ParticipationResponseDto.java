package com.example.domain.participation.dto;

import com.example.domain.participation.model.Participation;
import java.time.LocalDateTime;

public record ParticipationResponseDto(
        Long id,
        Long memberId,
        LocalDateTime joinedAt
) {
    public static ParticipationResponseDto fromEntity(Participation p) {
        return new ParticipationResponseDto(
                p.getId(),
                p.getMemberId(),
                p.getJoinedAt()
        );
    }
}
