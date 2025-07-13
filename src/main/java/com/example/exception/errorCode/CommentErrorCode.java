package com.example.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    ONLY_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "그룹 참여자만 댓글을 달 수 있습니다"),
    ONLY_WRITER_MEMBER_UPDATE(HttpStatus.BAD_REQUEST, "댓글은 작성자만 수정할 수 있습니다"),
    ONLY_WRITER_MEMBER_DELETE(HttpStatus.BAD_REQUEST, "댓글은 작성자만 삭제할 수 있습니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
