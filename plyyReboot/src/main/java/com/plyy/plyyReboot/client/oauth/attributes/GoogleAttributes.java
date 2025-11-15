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
        String emailValue = validateRequired(
                attributes.get("email"),
                "email",
                String.class
        );
        return Email.of(emailValue);
    }

    @Override
    public ProviderId providerId() {
        String subValue = validateRequired(
                attributes.get("sub"),
                "sub",
                String.class
        );
        return ProviderId.of(subValue);
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.GOOGLE;
    }

}
