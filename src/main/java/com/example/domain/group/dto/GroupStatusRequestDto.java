package com.example.domain.group.dto;

import com.example.domain.group.model.GroupStatus;
import jakarta.validation.constraints.NotNull;

public record GroupStatusRequestDto(
        @NotNull(message = "status는 필수입니다")
        GroupStatus status
) {}
