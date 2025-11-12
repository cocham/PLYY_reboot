package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

public final class GoogleAttributes implements OAuth2Attributes {
    private final Map<String, Object> attributes;

    public GoogleAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Google 속성 맵은 null일 수 없습니다.");
        }

        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Email email() {
        Object emailValue = attributes.get("email");

        if (emailValue == null) {
            throw new IllegalStateException("Google OAuth 공급자가 이메일을 제공하지 않았습니다.");
        }

        if (!(emailValue instanceof String emailString)) {
            throw new IllegalStateException("Google 이메일 값이 문자열이 아닙니다: " + emailValue.getClass().getName());
        }

        if (emailString.isBlank()) {
            throw new IllegalStateException("Google 이메일 값이 비어 있습니다.");
        }

        return Email.of(emailString);
    }

    @Override
    public ProviderId providerId() {
        Object subValue = attributes.get("sub");

        if (subValue == null) {
            throw new IllegalStateException("Google OAuth 공급자가 ID(sub)를 제공하지 않았습니다.");
        }

        if (!(subValue instanceof String subString)) {
            throw new IllegalStateException("Google 사용자 ID가 문자열이 아닙니다: " + subValue.getClass().getName());
        }

        if (subString.isBlank()) {
            throw new IllegalStateException("Google 사용자 ID 값이 비어 있습니다.");
        }

        return ProviderId.of(subString);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public Map<String, Object> raw() {
        return attributes;
    }
}