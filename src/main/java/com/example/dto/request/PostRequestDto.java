package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.model.GroupPost;
import com.example.model.PurchaseGroup;

public record PostRequestDto(
        @NotBlank(message = "게시글 제목은 비어 있을 수 없습니다")
        @Size(max = 100, message = "게시글 제목은 100자 이하여야 합니다")
        String title,

        @NotBlank(message = "게시글 내용은 비어 있을 수 없습니다")
        @Size(max = 500, message = "게시글 내용은 500자 이하여야 합니다")
        String content
) {
    public GroupPost toEntity(PurchaseGroup group, Long hostId) {
        return GroupPost.builder()
                .group(group)
                .hostId(hostId)
                .title(title)
                .content(content)
                .build();
    }
}
