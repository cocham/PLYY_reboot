package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.InvalidProviderException;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth 제공자 열거형
 */
@Slf4j
public enum AuthProvider {
    KAKAO, NAVER, GOOGLE;

    /**
     * Registration ID로부터 AuthProvider 생성
     *
     * @param id OAuth2 Registration ID (예: "kakao", "naver", "google")
     * @return 매칭되는 AuthProvider
     * @throws InvalidProviderException 지원하지 않는 Provider일 때
     */

    public static AuthProvider fromRegistrationId(String id) {
        if (id == null) {
            log.error("AuthProvider creation failed: null registration ID");
            throw new InvalidProviderException("OAuth 제공자 정보가 없습니다.");
        }

        String provider = id.trim().toLowerCase();

        return switch (provider) {
            case "kakao" -> KAKAO;
            case "naver" -> NAVER;
            case "google" -> GOOGLE;
            default -> {
                log.error("AuthProvider creation failed: unsupported provider: {}", id);
                throw new InvalidProviderException(
                        String.format("지원하지 않는 OAuth 제공자입니다: %s", id)
                );
            }
        };
    }
}



