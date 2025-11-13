package com.plyy.plyyReboot.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Optional;

/**
 * JWT 토큰 파싱 및 검증을 담당하는 클래스
 * 단일 책임: 토큰 파싱과 검증만 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenParser {

    private final SecretKey secretKey;

    /**
     * 토큰을 파싱하여 Claims 추출
     *
     * @param token JWT 토큰
     * @return TokenClaims 객체 (파싱 실패 시 Optional.empty())
     */
    public Optional<TokenClaims> parse(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new TokenClaims(claims));

        } catch (JwtException e) {
            log.debug("토큰 파싱 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (JwtException e) {
            log.debug("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 userId 추출 (편의 메서드)
     */
    public Optional<Long> extractUserId(String token) {
        return parse(token)
                .map(TokenClaims::getUserId);
    }

    /**
     * 토큰에서 role 추출 (편의 메서드)
     */
    public Optional<String> extractRole(String token) {
        return parse(token)
                .map(TokenClaims::getRole);
    }

    /**
     * 토큰의 남은 유효 시간 추출 (편의 메서드)
     */
    public Optional<Long> extractRemainingMillis(String token) {
        return parse(token)
                .map(TokenClaims::getRemainingMillis);
    }
}
