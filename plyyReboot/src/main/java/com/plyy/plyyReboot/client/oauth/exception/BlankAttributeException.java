package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import lombok.Getter;

@Getter
public class BlankAttributeException extends BaseAuthException {

    private final AuthProvider provider;
    private final String fieldName;

    public BlankAttributeException(AuthProvider provider, String fieldName, String userMessage) {
        super(userMessage, AuthErrorCode.BLANK_ATTRIBUTE);
        this.provider = provider;
        this.fieldName = fieldName;
    }

    public String getDetailInfo() {
        return String.format("[provider=%s, field=%s]", provider, fieldName);
    }
}