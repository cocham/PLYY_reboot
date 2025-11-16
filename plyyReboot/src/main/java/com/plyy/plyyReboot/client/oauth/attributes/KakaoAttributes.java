package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

public final class KakaoAttributes extends OAuth2Attributes {

    public KakaoAttributes(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Email email() {
        String rawEmail = getNestedField("kakao_account", "email", String.class);
        return createEmail(rawEmail);
    }

    @Override
    public ProviderId providerId() {
        Long rawId = validateRequired(attributes.get("id"), "id", Long.class);
        return createProviderId(rawId.toString());
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.KAKAO;
    }
}
