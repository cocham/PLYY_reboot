package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import lombok.Getter;

@Getter
public class MissingAttributeException extends BaseAuthException {

    private final AuthProvider provider;
    private final String fieldName;

    /**
     * 필드 정보를 포함하는 생성자
     */
    public MissingAttributeException(AuthProvider provider, String fieldName, String userMessage) {
        super(userMessage, AuthErrorCode.MISSING_ATTRIBUTE);
        this.provider = provider;
        this.fieldName = fieldName;
    }

    /**
     * 필드명 없이 생성 (하위 호환)
     */
    public MissingAttributeException(String userMessage) {
        super(userMessage, AuthErrorCode.MISSING_ATTRIBUTE);
        this.provider = null;
        this.fieldName = null;
    }

    /**
     * 로그용 상세 정보
     */
    public String getDetailInfo() {
        if (provider != null && fieldName != null) {
            return String.format("[provider=%s, field=%s]", provider, fieldName);
        }
        return "";
    }

}
