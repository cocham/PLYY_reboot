package com.plyy.plyyReboot.client.oauth.dto;

// (카카오, 네이버, 구글을 하나로 묶는 공통 인터페이스)
public interface OAuth2UserInfo {
    String getProvider(); // "kakao", "naver", "google"
    String getProviderId(); // 소셜의 고유 ID
    String getEmail();
    String getNickname();
    String getProfileImageUrl();
}