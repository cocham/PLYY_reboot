package com.plyy.plyyReboot.config.security.jwt.exception;

import com.plyy.plyyReboot.exception.ErrorCodeInterface;

/**
 * 토큰 관련 에러 코드
 */
public enum TokenErrorCode implements ErrorCodeInterface {
    TOKEN_ISSUANCE_FAIL("TOKEN_001", "토큰 발급에 실패했습니다."),
    TOKEN_REFRESH_FAIL("TOKEN_002", "토큰 갱신에 실패했습니다."),
    TOKEN_REVOCATION_FAIL("TOKEN_003", "토큰 무효화에 실패했습니다."),
    TOKEN_INVALID("TOKEN_004", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED("TOKEN_005", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND("TOKEN_006", "Refresh Token을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH("TOKEN_007", "Refresh Token이 일치하지 않습니다."),
    MISSING_AUTHORIZATION_HEADER("TOKEN_008", "Authorization 헤더가 없습니다."),
    INVALID_AUTHORIZATION_FORMAT("TOKEN_009", "Authorization 헤더 형식이 올바르지 않습니다.");

    private final String code;
    private final String message;

    TokenErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}