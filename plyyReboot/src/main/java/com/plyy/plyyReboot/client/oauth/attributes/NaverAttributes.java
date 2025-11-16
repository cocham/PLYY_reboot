package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

@SuppressWarnings("unchecked")
public final class NaverAttributes extends OAuth2Attributes {

    public NaverAttributes(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Email email() {
        String rawEmail = getNestedField("response", "email", String.class);
        return createEmail(rawEmail);
    }

    @Override
    public ProviderId providerId() {
        String rawId = getNestedField("response", "id", String.class);
        return createProviderId(rawId);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.NAVER;
    }
}
