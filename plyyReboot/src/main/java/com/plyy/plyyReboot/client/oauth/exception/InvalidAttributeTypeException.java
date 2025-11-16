package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import lombok.Getter;

@Getter
public class InvalidAttributeTypeException extends BaseAuthException {

    private final AuthProvider provider;
    private final String fieldName;
    private final String expectedType;
    private final String actualType;

    public InvalidAttributeTypeException(
            AuthProvider provider,
            String fieldName,
            String expectedType,
            String actualType,
            String userMessage
    ) {
        super(userMessage, AuthErrorCode.INVALID_ATTRIBUTE_TYPE);
        this.provider = provider;
        this.fieldName = fieldName;
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    public String getDetailInfo() {
        return String.format("[provider=%s, field=%s, expected=%s, actual=%s]",
                provider, fieldName, expectedType, actualType);
    }
}