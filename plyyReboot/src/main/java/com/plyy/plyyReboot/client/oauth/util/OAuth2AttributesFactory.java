package com.plyy.plyyReboot.client.oauth.util;

import com.plyy.plyyReboot.client.oauth.attributes.GoogleAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.KakaoAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.NaverAttributes;
import com.plyy.plyyReboot.client.oauth.attributes.OAuth2Attributes;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

public final class OAuth2AttributesFactory {
    private OAuth2AttributesFactory() {}

    public static OAuth2Attributes of(AuthProvider provider, Map<String, Object> attributes) {
        if (provider == null) {
            throw new IllegalArgumentException("인증 공급자(AuthProvider)는 null일 수 없습니다.");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("OAuth2 속성 맵은 null일 수 없습니다.");
        }

        return switch (provider) {
            case KAKAO -> new KakaoAttributes(attributes);
            case NAVER -> new NaverAttributes(attributes);
            case GOOGLE -> new GoogleAttributes(attributes);
        };
    }
}
