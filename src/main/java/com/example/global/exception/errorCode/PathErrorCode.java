package com.example.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PathErrorCode implements ErrorCode {
    NOT_VALID_PATH(HttpStatus.BAD_REQUEST, "잘못된 경로입니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
