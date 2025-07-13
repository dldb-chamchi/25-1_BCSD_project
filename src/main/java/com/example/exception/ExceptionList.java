package com.example.exception;

import com.example.exception.errorCode.ErrorCode;
import lombok.Getter;

@Getter
public class ExceptionList extends RuntimeException {
    private final ErrorCode errorCode;

    public ExceptionList(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
