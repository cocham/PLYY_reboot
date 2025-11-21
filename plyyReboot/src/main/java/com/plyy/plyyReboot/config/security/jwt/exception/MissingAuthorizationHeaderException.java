package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * Authorization 헤더가 없거나 형식이 잘못된 경우 발생하는 예외
 */
public class MissingAuthorizationHeaderException extends BaseTokenException {

    public MissingAuthorizationHeaderException(String message) {
        super(message, TokenErrorCode.MISSING_AUTHORIZATION_HEADER);
    }

    public MissingAuthorizationHeaderException() {
        super(TokenErrorCode.MISSING_AUTHORIZATION_HEADER);
    }
}
