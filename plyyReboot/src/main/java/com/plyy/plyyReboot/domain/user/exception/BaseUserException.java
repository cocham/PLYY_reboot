package com.plyy.plyyReboot.domain.user.exception;

import com.plyy.plyyReboot.exception.BaseRuntimeException;
import com.plyy.plyyReboot.exception.ErrorCodeInterface;
import lombok.Getter;

@Getter
public abstract class BaseUserException extends BaseRuntimeException {

    public BaseUserException(ErrorCodeInterface errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
    }

    public BaseUserException(ErrorCodeInterface errorCode, String message) {
        super(message, errorCode.getCode());
    }
}
