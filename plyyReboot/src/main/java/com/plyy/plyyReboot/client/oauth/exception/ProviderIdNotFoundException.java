package com.plyy.plyyReboot.client.oauth.exception;

public class ProviderIdNotFoundException extends BaseAuthException {
    public ProviderIdNotFoundException() {
        super(AuthErrorCode.PROVIDER_ID_NOT_FOUND);
    }
}
