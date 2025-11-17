package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * Refresh Token이 일치하지 않을 때 발생하는 예외
 */
public class RefreshTokenMismatchException extends BaseTokenException {

    public RefreshTokenMismatchException(String message) {
        super(message, TokenErrorCode.REFRESH_TOKEN_MISMATCH);
    }

    public RefreshTokenMismatchException() {
        super(TokenErrorCode.REFRESH_TOKEN_MISMATCH);
    }
}
