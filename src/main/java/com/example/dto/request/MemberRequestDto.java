package com.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.example.model.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public record MemberRequestDto(
        @NotBlank(message = "email을 입력해야 합니다")
        @Email(message = "email 형식으로 입력해야 합니다 (ex. example@test.com)")
        String email,

        @NotBlank(message = "password를 입력해야 합니다")
        String password,

        @NotBlank(message = "이름을 입력해야 합니다")
        String name
) {
    public Member toEntity(PasswordEncoder encoder) {
        return Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .name(name)
                .build();
    }
}

