package com.plyy.plyyReboot.client.oauth.exception;

public class InvalidProviderException extends BaseAuthException {
    public InvalidProviderException() {
        super(AuthErrorCode.INVALID_PROVIDER);
    }
}
