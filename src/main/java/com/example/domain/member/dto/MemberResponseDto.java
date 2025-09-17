package com.example.domain.member.dto;

import com.example.domain.member.model.Member;
import java.time.LocalDateTime;

public record MemberResponseDto(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt
) {
    public static MemberResponseDto fromEntity(Member m) {
        return new MemberResponseDto(
                m.getId(),
                m.getEmail(),
                m.getName(),
                m.getCreatedAt()
        );
    }
}
