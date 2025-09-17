package com.example.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.domain.post.model.Post;
import com.example.domain.group.model.Group;

public record PostRequestDto(
        @NotBlank(message = "게시글 제목은 비어 있을 수 없습니다")
        @Size(max = 100, message = "게시글 제목은 100자 이하여야 합니다")
        String title,

        @NotBlank(message = "게시글 내용은 비어 있을 수 없습니다")
        @Size(max = 500, message = "게시글 내용은 500자 이하여야 합니다")
        String content
) {
    public Post toEntity(Group group, Long hostId) {
        return Post.builder()
                .group(group)
                .hostId(hostId)
                .title(title)
                .content(content)
                .build();
    }
}
