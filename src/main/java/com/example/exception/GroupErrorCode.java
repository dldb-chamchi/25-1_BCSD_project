package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {
    NOT_FOUND_GROUP(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다"),
    HOST_ONLY_GROUP_UPDATE(HttpStatus.BAD_REQUEST, "그룹은 호스트만 수정할 수 있습니다"),
    HOST_ONLY_GROUP_DELETE(HttpStatus.BAD_REQUEST, "그룹은 호스트만 삭제할 수 있습니다"),
    NOT_DELETE_WITH_PARTICIPATION(HttpStatus.BAD_REQUEST, "참여자가 남아있는 그룹은 삭제할 수 없습니다"),
    NOT_DELETE_WITH_POST(HttpStatus.BAD_REQUEST, "게시글이 존재하는 그룹은 삭제할 수 없습니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
