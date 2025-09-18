package com.example.domain.group.dto;

import com.example.domain.group.model.GroupStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.example.domain.group.model.Group;

public record GroupRequestDto(
        @NotBlank(message = "그룹명은 비어 있을 수 없습니다")
        @Size(max = 100, message = "그룹명은 100자 이하여야 합니다")
        String title,

        @NotBlank(message = "그룹 설명은 비어 있을 수 없습니다")
        @Size(max = 500, message = "그룹 설명은 500자 이하여야 합니다")
        String description,

        @NotNull(message = "마감기한은 필수입니다")
        @Future(message = "마감기한은 미래여야 합니다")
        LocalDateTime expiresAt,

        @NotNull(message = "그룹 인원은 필수입니다")
        @Min(value = 2,   message = "그룹 인원은 최소 2명이상이어야 합니다")
        @Max(value = 100, message = "그룹 인원은 최대 100명이하이어야 합니다")
        Integer maxMember
) {
    public Group toEntity(Long hostId) {
        return Group.builder()
                .hostId(hostId)
                .title(title)
                .description(description)
                .expiresAt(expiresAt)
                .maxMember(maxMember)
                .createdAt(LocalDateTime.now())
                .status(GroupStatus.OPEN)
                .build();
    }
}
