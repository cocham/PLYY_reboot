package com.plyy.plyyReboot.config.security.jwt.exception;

public class InvalidAuthorizationHeaderFormatException extends BaseTokenException {

    public InvalidAuthorizationHeaderFormatException(String message) {
        super(message, TokenErrorCode.INVALID_AUTHORIZATION_FORMAT);
    }

    public InvalidAuthorizationHeaderFormatException() {
        super(TokenErrorCode.INVALID_AUTHORIZATION_FORMAT);
    }
}