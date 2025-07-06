// src/main/java/com/example/dto/response/MemberResponseDto.java
package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
}
