package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.exception.BaseRuntimeException;
import com.plyy.plyyReboot.exception.ErrorCodeInterface;

public abstract class BaseAuthException extends BaseRuntimeException {
    private final ErrorCodeInterface errorCode;

    public BaseAuthException(ErrorCodeInterface errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BaseAuthException(String message, ErrorCodeInterface errorCode) {
        super(message, errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BaseAuthException(ErrorCodeInterface errorCode, Throwable cause) {
        super(errorCode.getMessage(), errorCode.getCode(), cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}