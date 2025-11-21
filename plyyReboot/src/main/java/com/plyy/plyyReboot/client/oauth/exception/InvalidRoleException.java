package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.exception.BaseRuntimeException;

public class InvalidRoleException extends BaseRuntimeException {

    public InvalidRoleException(String debugMessage) {
        super(AuthErrorCode.INVALID_ROLE.getMessage() + " (" + debugMessage + ")",
                AuthErrorCode.INVALID_ROLE.getCode());
    }
}
