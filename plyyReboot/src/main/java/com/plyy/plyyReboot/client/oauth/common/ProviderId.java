package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.ProviderIdNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Provider ID 값 객체
 * - OAuth 로그인: fromValidated() 사용 (이미 검증된 값)
 * - 일반 입력: of() 사용 (완전한 검증 수행)
 */
@Slf4j
public final class ProviderId {
    private final String value;

    private ProviderId(String value) {
        this.value = value;
    }

    /**
     * OAuth2Attributes에서 사용 (null/blank는 이미 검증됨)
     */
    public static ProviderId fromValidated(String value) {
        return new ProviderId(value);
    }

    /**
     * 외부에서 직접 생성 시 사용
     * 완전한 검증 수행
     */
    public static ProviderId of(String raw) {
        if (raw == null) {
            log.error("ProviderId 생성 실패: null 값");
            throw new ProviderIdNotFoundException("Provider ID는 필수 정보입니다.");
        }

        if (raw.isBlank()) {
            log.error("ProviderId 생성 실패: 빈 값");
            throw new ProviderIdNotFoundException("Provider ID는 비어 있을 수 없습니다.");
        }

        return new ProviderId(raw.trim());
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
        if (!(obj instanceof ProviderId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
