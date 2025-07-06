// src/main/java/com/example/dto/request/MemberRequestDto.java
package com.example.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberRequestDto {
    private String email;
    private String password;
    private String name;
}
