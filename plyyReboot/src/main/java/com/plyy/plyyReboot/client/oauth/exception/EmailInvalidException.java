package com.plyy.plyyReboot.client.oauth.exception;

public class EmailInvalidException extends BaseAuthException {
    public EmailInvalidException() {
        super(AuthErrorCode.EMAIL_INVALID);
    }
}
