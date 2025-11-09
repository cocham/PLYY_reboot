package com.plyy.plyyReboot.config.security.jwt;

import com.plyy.plyyReboot.web.api.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // ★ SecretKey 필요
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24h
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L; // 7d

    @PostConstruct
    protected void init() {
        // HS 계열 키 생성
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse createTokens(Long userId, String role) {
        String accessToken = createToken(userId, role, ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = createToken(userId, role, REFRESH_TOKEN_EXPIRATION_MS);
        return new TokenResponse(accessToken, refreshToken);
    }

    private String createToken(Long userId, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("userId", userId)
                .claim("role", role)
                // 알고리즘 명시 (HS512 사용 시)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)       // parserBuilder() 아님!
                .build()
                .parseSignedClaims(token)    // parseClaimsJws() 아님!
                .getPayload();               // Claims 반환
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);   // 파싱만 성공해도 유효
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return getClaimsFromToken(token).get("userId", Number.class).longValue();
    }

    public String getRole(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    public long getRemainingMillis(String token) {
        Date exp = getClaimsFromToken(token).getExpiration();
        return Math.max(0L, exp.getTime() - System.currentTimeMillis());
    }

}
