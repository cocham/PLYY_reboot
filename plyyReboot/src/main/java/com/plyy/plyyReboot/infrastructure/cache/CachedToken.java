package com.plyy.plyyReboot.infrastructure.cache;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * CachedToken 확장 (만료 체크)
 */
public record CachedToken(
        String token,
        LocalDateTime expiresAt
) {
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValidWithBuffer(int bufferMinutes) {
        return LocalDateTime.now()
                .plusMinutes(bufferMinutes)
                .isBefore(expiresAt);
    }

    public long remainingSeconds() {
        long seconds = Duration.between(LocalDateTime.now(), expiresAt).toSeconds();
        return Math.max(0, seconds);
    }

    public static CachedToken of(String token, int expiresInSeconds) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(expiresInSeconds)
                .minusSeconds(60); // 60초 버퍼
        return new CachedToken(token, expiresAt);
    }
}
