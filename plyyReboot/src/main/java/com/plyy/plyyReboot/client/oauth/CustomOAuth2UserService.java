package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.client.oauth.attributes.OAuth2Attributes;
import com.plyy.plyyReboot.client.oauth.util.OAuth2AttributesFactory;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final SocialLoginService socialLoginService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User delegate = super.loadUser(request);
        Map<String, Object> raw = delegate.getAttributes();

        AuthProvider provider = AuthProvider.fromRegistrationId(
                request.getClientRegistration().getRegistrationId()
        );

        OAuth2Attributes attrs = OAuth2AttributesFactory.of(provider, raw);

        var user = socialLoginService.loginOrRegister(attrs);

        Map<String, Object> view = Map.of(
                "email", user.getEmail(),
                "provider", provider.name(),
                "providerId", attrs.providerId().value()
        );

        return new
                PlyyOAuth2Principal(
                user.getEmail(),
                user.getRole(),
                view
        );
    }
}