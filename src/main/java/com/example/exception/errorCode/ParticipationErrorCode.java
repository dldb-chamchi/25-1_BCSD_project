package com.example.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ParticipationErrorCode implements ErrorCode {
    NOT_FOUND_PARTICIPATION(HttpStatus.NOT_FOUND, "참여 내역을 찾을 수 없습니다"),
    NOT_PARTICIPATE_GROUP_CLOSED(HttpStatus.BAD_REQUEST, "마감된 그룹에는 참여할 수 없습니다"),
    LIMIT_MAX_MEMBER(HttpStatus.BAD_REQUEST, "모집 인원이 가득 찼습니다"),
    ALREADY_MEMBER(HttpStatus.BAD_REQUEST, "이미 이 그룹에 참여 중입니다"),
    NOT_LEAVE_HOST(HttpStatus.BAD_REQUEST, "호스트는 그룹에서 나갈 수 없습니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
