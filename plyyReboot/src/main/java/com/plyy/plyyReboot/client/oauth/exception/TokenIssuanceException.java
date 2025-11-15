package com.plyy.plyyReboot.client.oauth.exception;

public class TokenIssuanceException extends BaseAuthException {
    public TokenIssuanceException(String message) {
        super(AuthErrorCode.TOKEN_ISSUANCE_FAIL);
    }

    public TokenIssuanceException(Throwable cause) {
        super(AuthErrorCode.TOKEN_ISSUANCE_FAIL, cause);
    }
}