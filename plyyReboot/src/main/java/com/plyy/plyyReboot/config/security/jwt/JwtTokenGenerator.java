package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.config.security.jwt.exception.TokenIssuanceException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 생성을 담당하는 클래스
 * 단일 책임: 토큰 생성만 처리
 */
@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    /**
     * Access Token과 Refresh Token을 생성
     *
     * @param userId 사용자 ID
     * @param role 사용자 권한
     * @return 생성된 토큰 쌍
     */
    public TokenPair generateTokenPair(Long userId, String role) {
        String accessToken = createToken(
                userId,
                role,
                jwtProperties.getAccessTokenExpirationMs()
        );

        String refreshToken = createToken(
                userId,
                role,
                jwtProperties.getRefreshTokenExpirationMs()
        );

        return TokenPair.of(accessToken, refreshToken);
    }

    /**
     * 단일 토큰 생성
     */
    private String createToken(Long userId, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("userId", userId)
                .claim("role", role)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Access Token과 Refresh Token을 담는 불변 객체
     */
    public record TokenPair(String accessToken, String refreshToken) {
        public static TokenPair of(String accessToken, String refreshToken) {
            if (accessToken == null || accessToken.isBlank()) {
                throw new TokenIssuanceException("accessToken 값이 존재하지 않습니다.");
            }
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new TokenIssuanceException("refreshToken 값이 존재하지 않습니다.");
            }
            return new TokenPair(accessToken, refreshToken);
        }
    }
}