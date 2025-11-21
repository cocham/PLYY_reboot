package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.config.security.jwt.exception.InvalidAuthorizationHeaderFormatException;
import com.plyy.plyyReboot.config.security.jwt.exception.MissingAuthorizationHeaderException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * HTTP 요청에서 JWT 토큰을 추출하는 전략 클래스
 * - Access Token: Authorization 헤더 (Bearer)
 * - Refresh Token: HttpOnly 쿠키
 */
@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtProperties jwtProperties;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * [Access Token 추출] - Optional
     * Authorization 헤더에서만 추출 (Bearer 방식)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractFromHeader(request);
    }

    /**
     * [Access Token 추출] - 강제 (없으면 예외)
     * 로그아웃, 회원탈퇴 등 인증된 사용자만 접근하는 API에서 사용
     */
    public String extractAccessTokenOrThrow(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || header.isBlank()) {
            throw new MissingAuthorizationHeaderException("Authorization 헤더가 없습니다.");
        }

        if (!header.startsWith(BEARER_PREFIX)) {
            throw new InvalidAuthorizationHeaderFormatException(
                    "Authorization 헤더는 'Bearer {token}' 형식이어야 합니다."
            );
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new InvalidAuthorizationHeaderFormatException("토큰 값이 비어있습니다.");
        }

        return token;
    }

    /**
     * [Refresh Token 추출] - Optional
     * 토큰 재발급(Reissue) API에서 사용 (HttpOnly 쿠키)
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtProperties.getRefreshTokenCookieName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    // ================= 내부 헬퍼 메서드 =================

    private Optional<String> extractFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }
}