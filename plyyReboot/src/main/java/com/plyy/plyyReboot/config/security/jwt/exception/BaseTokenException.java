package com.plyy.plyyReboot.config.security.jwt.exception;

import com.plyy.plyyReboot.exception.BaseRuntimeException;
import com.plyy.plyyReboot.exception.ErrorCodeInterface;

/**
 * 토큰 관련 예외의 베이스 클래스
 */
public abstract class BaseTokenException extends BaseRuntimeException {

    private final ErrorCodeInterface errorCode;

    public BaseTokenException(ErrorCodeInterface errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BaseTokenException(String message, ErrorCodeInterface errorCode) {
        super(message, errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BaseTokenException(ErrorCodeInterface errorCode, Throwable cause) {
        super(errorCode.getMessage(), errorCode.getCode(), cause);
        this.errorCode = errorCode;
    }

    public BaseTokenException(String message, ErrorCodeInterface errorCode, Throwable cause) {
        super(message, errorCode.getCode(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode.getCode();
    }
}
