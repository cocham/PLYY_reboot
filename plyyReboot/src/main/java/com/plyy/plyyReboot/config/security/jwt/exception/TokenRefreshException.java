package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * 토큰 갱신 실패 시 발생하는 예외
 */
public class TokenRefreshException extends BaseTokenException {

    public TokenRefreshException(String message) {
        super(message, TokenErrorCode.TOKEN_REFRESH_FAIL);
    }

    public TokenRefreshException(String message, Throwable cause) {
        super(message, TokenErrorCode.TOKEN_REFRESH_FAIL, cause);
    }

    public TokenRefreshException(Throwable cause) {
        super(TokenErrorCode.TOKEN_REFRESH_FAIL, cause);
    }
}
