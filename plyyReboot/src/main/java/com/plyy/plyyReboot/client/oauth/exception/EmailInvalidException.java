package com.plyy.plyyReboot.client.oauth.exception;

public class EmailInvalidException extends BaseAuthException {
    public EmailInvalidException(String message) {
        super(message, AuthErrorCode.EMAIL_INVALID);
    }
}
