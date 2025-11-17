package com.plyy.plyyReboot.config.security.jwt.exception;

/**
 * 토큰 발급 실패 시 발생하는 예외
 */
public class TokenIssuanceException extends BaseTokenException {

    public TokenIssuanceException(String message) {
        super(message, TokenErrorCode.TOKEN_ISSUANCE_FAIL);
    }

    public TokenIssuanceException(String message, Throwable cause) {
        super(message, TokenErrorCode.TOKEN_ISSUANCE_FAIL, cause);
    }

    public TokenIssuanceException(Throwable cause) {
        super(TokenErrorCode.TOKEN_ISSUANCE_FAIL, cause);
    }
}