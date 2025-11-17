package com.plyy.plyyReboot.client.oauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 모든 경로에서 접근 가능함
        cookie.setMaxAge(maxAge); // 초 단위로 설정됨
        cookie.setHttpOnly(httpOnly); // JS 접근 차단 (refresh token에 필수임)
        cookie.setSecure(request.isSecure()); // HTTPS(운영)에서만 Secure 플래그 설정, 로컬(http)에서도 테스트 가능하게 변경함

        response.addCookie(cookie);
    }

    /**
     * 쿠키를 삭제하는 메서드
     * maxAge를 0으로 설정하여 즉시 만료시킵니다.
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키를 즉시 만료시킴
        cookie.setSecure(request.isSecure());
        cookie.setHttpOnly(false);

        response.addCookie(cookie);
    }
}
