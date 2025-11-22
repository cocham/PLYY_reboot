package com.plyy.plyyReboot.domain.playlist.exception;

import com.plyy.plyyReboot.exception.BaseRuntimeException;
import com.plyy.plyyReboot.exception.ErrorCodeInterface;

public abstract class BasePlaylistException extends BaseRuntimeException {

    private final ErrorCodeInterface errorCode;

    public BasePlaylistException(ErrorCodeInterface errorCode) {
        super(errorCode.getMessage(), errorCode.getCode());
        this.errorCode = errorCode;
    }

    public BasePlaylistException(ErrorCodeInterface errorCode, String message) {
        super(message, errorCode.getCode());
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }

    public ErrorCodeInterface getErrorCodeInterface() {
        return errorCode;
    }

    public String getDetailInfo() {
        return "";
    }
}