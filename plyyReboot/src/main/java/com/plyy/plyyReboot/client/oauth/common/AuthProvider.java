package com.plyy.plyyReboot.client.oauth.common;

public enum AuthProvider {
    KAKAO, NAVER, GOOGLE;

    public static AuthProvider fromRegistrationId(String id) {
        if (id == null) {
            throw new NullPointerException("Registration ID는 null일 수 없습니다.");
        }

        String provider = id.trim().toLowerCase();
        if (provider.equals("kakao")) return KAKAO;
        if (provider.equals("naver")) return NAVER;
        if (provider.equals("google")) return GOOGLE;

        throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + id);
    }
}



