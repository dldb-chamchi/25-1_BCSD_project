package com.example.dto.request;

import com.example.model.PurchaseGroupStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusDto(
        @NotNull(message = "status는 필수입니다")
        PurchaseGroupStatus status
) {}
