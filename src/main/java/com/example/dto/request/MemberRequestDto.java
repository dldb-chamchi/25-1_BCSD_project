package com.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberRequestDto {
    @NotBlank(message = "email을 입력해야 합니다")
    @Email(message = "email 형식으로 입력해야 합니다(ex. example@test.com")
    private String email;

    @NotBlank(message =  "password를 입력해야 합니다")
    private String password;

    @NotBlank(message = "이름을 입력해야 합니다")
    private String name;
}
