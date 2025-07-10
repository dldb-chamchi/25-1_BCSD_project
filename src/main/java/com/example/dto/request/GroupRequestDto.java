package com.example.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequestDto {
    @NotBlank(message = "그룹명은 비어 있을 수 없습니다")
    @Size(max = 100, message = "그룹명은 100자 이하여야 합니다")
    private String title;

    @NotNull
    @Size(max = 500, message = "그룹 설명은 500자 이하여야 합니다")
    private String description;

    @NotNull(message = "마감기한은 필수입니다")
    @Future(message = "마감기한은 미래여야 합니다")
    private LocalDateTime expiresAt;

    @NotNull(message = "그룹 인원은 필수입니다")
    @Range(min=2, max=100, message = "그룹 인원은 최소 2명이상, 최대 100명 이하여야 합니다")
    private Integer maxMembers;
}
