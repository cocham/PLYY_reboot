package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

@SuppressWarnings("unchecked")
public final class KakaoAttributes implements OAuth2Attributes {
    private final Map<String, Object> attributes;

    public KakaoAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Kakao 속성 맵은 null일 수 없습니다.");
        }
        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Email email() {
        Object accountValue = attributes.get("kakao_account");

        if (accountValue == null) {
            throw new IllegalStateException("Kakao OAuth 응답에 'kakao_account' 필드가 없습니다.");
        }

        if (!(accountValue instanceof Map<?, ?> accountMap)) {
            throw new IllegalStateException("Kakao 'kakao_account' 필드가 Map 형식이 아닙니다: " + accountValue.getClass().getName());
        }

        Object emailValue = accountMap.get("email");

        if (emailValue == null) {
            throw new IllegalStateException("Kakao OAuth 공급자가 이메일을 제공하지 않았습니다.");
        }

        if (!(emailValue instanceof String emailString)) {
            throw new IllegalStateException("Kakao 이메일 값이 문자열이 아닙니다: " + emailValue.getClass().getName());
        }

        if (emailString.isBlank()) {
            throw new IllegalStateException("Kakao 이메일 값이 비어 있습니다.");
        }

        return Email.of(emailString);
    }

    @Override
    public ProviderId providerId() {
        Object idValue = attributes.get("id");

        if (idValue == null) {
            throw new IllegalStateException("Kakao OAuth 응답에 'id' 필드가 없습니다.");
        }

        String idString = idValue.toString();

        if (idString.isBlank()) {
            throw new IllegalStateException("Kakao 사용자 ID 값이 비어 있습니다.");
        }

        return ProviderId.of(idString);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public Map<String, Object> raw() {
        return attributes;
    }
}