package com.example.dto.response;

import com.example.model.Member;
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
