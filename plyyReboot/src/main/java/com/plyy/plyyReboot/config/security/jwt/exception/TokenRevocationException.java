package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * 토큰 무효화 실패 시 발생하는 예외
 */
public class TokenRevocationException extends BaseTokenException {

    public TokenRevocationException(String message) {
        super(message, TokenErrorCode.TOKEN_REVOCATION_FAIL);
    }

    public TokenRevocationException(String message, Throwable cause) {
        super(message, TokenErrorCode.TOKEN_REVOCATION_FAIL, cause);
    }

    public TokenRevocationException(Throwable cause) {
        super(TokenErrorCode.TOKEN_REVOCATION_FAIL, cause);
    }
}