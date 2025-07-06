package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationResponseDto {
    private Long id;
    private Long memberId;
    private LocalDateTime joinedAt;
    private String paymentStatus;

}
