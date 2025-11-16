package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.EmailInvalidException;
import lombok.extern.slf4j.Slf4j;

/**
 * 이메일 값 객체
 * - OAuth 로그인: fromValidated() 사용 (이미 검증된 값)
 * - 일반 입력: of() 사용 (완전한 검증 수행)
 */
@Slf4j
public final class Email {
    private final String value;
    private static final String EMAIL_SYMBOL = "@";
    private static final String DOT = ".";

    private Email(String value) {
        this.value = value;
    }

    /**
     * OAuth2Attributes에서 사용 (null/blank는 이미 검증됨)
     * 이메일 형식만 검증
     */
    public static Email fromValidated(String value) {
        validateEmailFormat(value);
        return new Email(value);
    }

    public static Email of(String raw) {
        if (raw == null) {
            log.error("Email 생성 실패: null 값");
            throw new EmailInvalidException("이메일은 필수 정보입니다.");
        }

        if (raw.isBlank()) {
            log.error("Email 생성 실패: 빈 값");
            throw new EmailInvalidException("이메일은 비어 있을 수 없습니다.");
        }

        String trimmed = raw.trim();
        validateEmailFormat(trimmed);

        return new Email(trimmed);
    }

    /**
     * 이메일 형식 검증
     */
    private static void validateEmailFormat(String email) {
        if (!email.contains(EMAIL_SYMBOL)) {
            log.error("이메일 형식 오류: '@' 기호 없음 - {}", email);
            throw new EmailInvalidException("유효하지 않은 이메일 형식입니다.");
        }

        String[] parts = email.split(EMAIL_SYMBOL, -1);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            log.error("이메일 형식 오류: 로컬 또는 도메인 부분이 비어 있음 - {}", email);
            throw new EmailInvalidException("유효하지 않은 이메일 형식입니다.");
        }

        if (!parts[1].contains(DOT)) {
            log.error("이메일 형식 오류: 도메인에 점(.) 없음 - {}", email);
            throw new EmailInvalidException("유효하지 않은 이메일 도메인입니다.");
        }
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
