package com.example.global;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "email은 비어 있을 수 없습니다")
        String email,

        @NotBlank(message = "password는 비어 있을 수 없습니다")
        String password
) {

}
