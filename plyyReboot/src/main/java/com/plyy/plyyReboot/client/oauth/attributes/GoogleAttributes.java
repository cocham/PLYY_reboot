package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import java.util.Map;

public final class GoogleAttributes extends OAuth2Attributes {

    public GoogleAttributes(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Email email() {
        String rawEmail = validateRequired(
                attributes.get("email"),
                "email",
                String.class);

        return createEmail(rawEmail);
    }

    @Override
    public ProviderId providerId() {
        String rawId = validateRequired(attributes.get("sub"), "sub", String.class);

        return createProviderId(rawId);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.GOOGLE;
    }
}
