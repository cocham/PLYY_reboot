package com.plyy.plyyReboot.client.oauth.util;

import com.plyy.plyyReboot.client.oauth.attributes.GoogleAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.KakaoAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.NaverAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.OAuth2Attributes;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import com.plyy.plyyReboot.client.oauth.exception.InvalidProviderException;
import com.plyy.plyyReboot.client.oauth.exception.MissingAttributeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OAuth2Attributes 생성 팩토리
 * Provider에 따라 적절한 Attributes 객체를 생성
 */
@Slf4j
public final class OAuth2AttributesFactory {

    private OAuth2AttributesFactory() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다");
    }

    /**
     * AuthProvider에 따라 적절한 OAuth2Attributes 생성
     *
     * @param provider OAuth 제공자
     * @param attributes OAuth 응답 속성
     * @return Provider에 맞는 Attributes 객체
     * @throws InvalidProviderException provider가 null이거나 지원하지 않는 경우
     * @throws MissingAttributeException attributes가 null인 경우
     */
    public static OAuth2Attributes of(AuthProvider provider, Map<String, Object> attributes) {
        if (provider == null) {
            log.error("OAuth2Attributes 생성 실패: AuthProvider가 null");
            throw new InvalidProviderException("인증 제공자 정보가 없습니다.");
        }

        if (attributes == null) {
            log.error("OAuth2Attributes 생성 실패: attributes가 null, provider={}", provider);
            throw new MissingAttributeException("인증 정보를 받지 못했습니다.");
        }

        log.debug("OAuth2Attributes 생성: provider={}", provider);

        return switch (provider) {
            case KAKAO -> new KakaoAttributes(attributes);
            case NAVER -> new NaverAttributes(attributes);
            case GOOGLE -> new GoogleAttributes(attributes);
        };
    }
}