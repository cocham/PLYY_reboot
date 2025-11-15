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
        String emailValue = getNestedField("kakao_account", "email", String.class);
        return Email.of(emailValue);
    }

    @Override
    public ProviderId providerId() {
        Object idValue = validateRequired(
                attributes.get("id"),
                "id",
                Object.class
        );
        return ProviderId.of(idValue.toString());
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.KAKAO;
    }
}
