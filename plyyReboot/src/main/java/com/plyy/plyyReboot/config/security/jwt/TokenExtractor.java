package com.plyy.plyyReboot.config.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * HTTP 요청에서 JWT 토큰을 추출하는 전략 클래스
 * 다양한 소스(쿠키, 헤더 등)에서 토큰 추출 지원
 */
@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtProperties jwtProperties;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * HTTP 요청에서 토큰 추출 (쿠키 우선, 헤더 대체)
     *
     * @param request HTTP 요청
     * @return 추출된 토큰 (없으면 Optional.empty())
     */
    public Optional<String> extract(HttpServletRequest request) {
        // 1. 쿠키에서 추출 시도
        Optional<String> tokenFromCookie = extractFromCookie(request);
        if (tokenFromCookie.isPresent()) {
            return tokenFromCookie;
        }

        // 2. Authorization 헤더에서 추출 시도
        return extractFromHeader(request);
    }

    /**
     * 쿠키에서 토큰 추출
     */
    public Optional<String> extractFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtProperties.getCookieName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    /**
     * Authorization 헤더에서 토큰 추출
     * 형식: "Bearer {token}"
     */
    public Optional<String> extractFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }
}