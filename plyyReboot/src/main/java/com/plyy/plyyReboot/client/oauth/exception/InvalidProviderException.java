package com.plyy.plyyReboot.client.oauth.exception;

public class InvalidProviderException extends BaseAuthException {
    public InvalidProviderException(String message) {
        super(message, AuthErrorCode.INVALID_PROVIDER);
    }
}
