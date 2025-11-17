package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * Refresh Token을 찾을 수 없을 때 발생하는 예외
 */
public class RefreshTokenNotFoundException extends BaseTokenException {

    public RefreshTokenNotFoundException(String message) {
        super(message, TokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    public RefreshTokenNotFoundException() {
        super(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
