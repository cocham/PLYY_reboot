package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

@SuppressWarnings("unchecked")
public final class NaverAttributes implements OAuth2Attributes {
    private final Map<String, Object> attributes;

    public NaverAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Naver 속성 맵은 null일 수 없습니다.");
        }
        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Email email() {
        Object responseValue = attributes.get("response");

        if (responseValue == null) {
            throw new IllegalStateException("Naver OAuth 응답에 'response' 필드가 없습니다.");
        }

        if (!(responseValue instanceof Map<?, ?> responseMap)) {
            throw new IllegalStateException("Naver 'response' 필드가 Map 형식이 아닙니다: " + responseValue.getClass().getName());
        }

        Object emailValue = responseMap.get("email");

        if (emailValue == null) {
            throw new IllegalStateException("Naver OAuth 공급자가 이메일을 제공하지 않았습니다.");
        }

        if (!(emailValue instanceof String emailString)) {
            throw new IllegalStateException("Naver 이메일 값이 문자열이 아닙니다: " + emailValue.getClass().getName());
        }

        if (emailString.isBlank()) {
            throw new IllegalStateException("Naver 이메일 값이 비어 있습니다.");
        }

        return Email.of(emailString);
    }

    @Override
    public ProviderId providerId() {
        Object responseValue = attributes.get("response");

        if (responseValue == null) {
            throw new IllegalStateException("Naver OAuth 응답에 'response' 필드가 없습니다.");
        }

        if (!(responseValue instanceof Map<?, ?> responseMap)) {
            throw new IllegalStateException("Naver 'response' 필드가 Map 형식이 아닙니다: " + responseValue.getClass().getName());
        }

        Object idValue = responseMap.get("id");

        if (idValue == null) {
            throw new IllegalStateException("Naver OAuth 공급자가 ID를 제공하지 않았습니다.");
        }

        if (!(idValue instanceof String idString)) {
            throw new IllegalStateException("Naver 사용자 ID가 문자열이 아닙니다: " + idValue.getClass().getName());
        }

        if (idString.isBlank()) {
            throw new IllegalStateException("Naver 사용자 ID 값이 비어 있습니다.");
        }

        return ProviderId.of(idString);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.NAVER;
    }

    @Override
    public Map<String, Object> raw() {
        return attributes;
    }
}
