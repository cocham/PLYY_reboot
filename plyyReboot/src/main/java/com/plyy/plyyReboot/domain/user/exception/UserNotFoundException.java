package com.plyy.plyyReboot.domain.user.exception;

public class UserNotFoundException extends BaseUserException {

    public UserNotFoundException(String debugMessage) {
        super(UserErrorCode.USER_NOT_FOUND,
                UserErrorCode.USER_NOT_FOUND.getMessage() + " (" + debugMessage + ")");
    }

    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND,
                UserErrorCode.USER_NOT_FOUND.getMessage() + " (ID: " + userId + ")");
    }
}