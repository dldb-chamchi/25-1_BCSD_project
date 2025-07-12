package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),
    HOST_ONLY_POST_UPLOAD(HttpStatus.BAD_REQUEST, "게시글 업로드는 호스트만 가능합니다"),
    HOST_ONLY_POST_UPDATE(HttpStatus.BAD_REQUEST, "게시글 수정은 호스트만 가능합니다"),
    HOST_ONLY_POST_DELETE(HttpStatus.BAD_REQUEST, "게시글 삭제는 호스트만 가능합니다"),
    NOT_DELETE_WITH_COMMENT(HttpStatus.BAD_REQUEST, "댓글이 달린 게시글은 삭제할 수 없습니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
