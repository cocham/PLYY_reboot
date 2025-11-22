package com.plyy.plyyReboot.config.security.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Redis 기반 토큰 관리 서비스
 * Refresh Token 및 Denylist(로그아웃 토큰) 관리 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RefreshTokenManager refreshTokenManager;
    private final TokenDenylistManager denylistManager;

    // ========== Refresh Token 위임 메서드 ==========

    public void saveRefreshToken(Long userId, String refreshToken) {
        refreshTokenManager.save(userId, refreshToken);
    }

    public String getRefreshToken(Long userId) {
        return refreshTokenManager.get(userId);
    }

    public void validateRefreshToken(Long userId, String providedToken) {
        refreshTokenManager.validate(userId, providedToken);
    }

    public boolean hasRefreshToken(Long userId) {
        return refreshTokenManager.exists(userId);
    }

    public boolean deleteRefreshToken(Long userId) {
        return refreshTokenManager.delete(userId);
    }

    // ========== Denylist 위임 메서드 ==========

    public void addToDenylist(String accessToken, long expirationMillis) {
        denylistManager.add(accessToken, expirationMillis);
    }

    public boolean isTokenInDenylist(String accessToken) {
        return denylistManager.contains(accessToken);
    }
}
