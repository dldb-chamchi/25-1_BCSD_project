package com.example.dto.response;

import com.example.model.Participation;
import java.time.LocalDateTime;

public record ParticipationResponseDto(
        Long id,
        Long memberId,
        LocalDateTime joinedAt,
        String paymentStatus
) {
    public static ParticipationResponseDto fromEntity(Participation p) {
        return new ParticipationResponseDto(
                p.getId(),
                p.getMemberId(),
                p.getJoinedAt(),
                p.getPaymentStatus()
        );
    }
}
