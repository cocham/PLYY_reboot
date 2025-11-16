package com.plyy.plyyReboot.client.oauth.exception;

public class ProviderIdNotFoundException extends BaseAuthException {
    public ProviderIdNotFoundException(String message) {
        super(message, AuthErrorCode.PROVIDER_ID_NOT_FOUND);
    }
}
