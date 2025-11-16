package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import lombok.Getter;

@Getter
public class InvalidResponseStructureException extends BaseAuthException {

    private final AuthProvider provider;
    private final String missingKey;
    private final String actualType;

    public InvalidResponseStructureException(
            AuthProvider provider,
            String missingKey,
            String actualType,
            String userMessage
    ) {
        super(userMessage, AuthErrorCode.INVALID_RESPONSE_STRUCTURE);
        this.provider = provider;
        this.missingKey = missingKey;
        this.actualType = actualType;
    }

    public String getDetailInfo() {
        if (actualType != null) {
            return String.format("[provider=%s, key=%s, actualType=%s]",
                    provider, missingKey, actualType);
        }
        return String.format("[provider=%s, key=%s]", provider, missingKey);
    }
}