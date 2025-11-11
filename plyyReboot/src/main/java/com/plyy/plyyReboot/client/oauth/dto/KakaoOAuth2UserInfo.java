package com.plyy.plyyReboot.client.oauth.dto;

import java.util.Map;

@SuppressWarnings("unchecked") // Map 타입 캐스팅 경고 무시
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;
    private final Map<String, Object> properties;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // 카카오는 id가 Long 타입이므로 String으로 변환
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }
}