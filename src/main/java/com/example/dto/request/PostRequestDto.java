package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    @NotBlank(message = "게시글 제목은 비어 있을 수 없습니다")
    @Size(max = 100, message = "게시글 제목은 100자 이하여야 합니다")
    private String title;

    @NotBlank(message = "게시글 내용은 비어 있을 수 없습니다")
    @Size(max = 500, message = "게시글 내용은 500자 이하여야 합니다")
    private String content;
}
