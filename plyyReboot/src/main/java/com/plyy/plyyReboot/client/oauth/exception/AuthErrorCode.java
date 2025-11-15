package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.exception.ErrorCodeInterface;

public enum AuthErrorCode implements ErrorCodeInterface {
    INVALID_PROVIDER("AUTH_001", "유효하지 않은 인증 제공자입니다."),
    MISSING_ATTRIBUTE("AUTH_002", "필수 속성이 누락되었습니다."),
    EMAIL_INVALID("AUTH_003", "유효하지 않은 이메일 형식입니다."),
    PROVIDER_ID_NOT_FOUND("AUTH_004", "제공자 ID를 찾을 수 없습니다."),
    TOKEN_ISSUANCE_FAIL("AUTH_005", "토큰 발급 중 오류가 발생했습니다.");

    private final String code;
    private final String message;

    AuthErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}

