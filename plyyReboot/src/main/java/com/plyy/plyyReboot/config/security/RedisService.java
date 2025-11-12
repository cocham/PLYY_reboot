package com.plyy.plyyReboot.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);

    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "RT:" + userId.toString();

        log.info("[Redis] SET 시도: Key={}, Value={}", key, refreshToken);
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_DURATION);
        String valueFromRedis = redisTemplate.opsForValue().get(key);
        if (valueFromRedis == null) {
            log.error("[Redis] SET 직후 GET 실패! (nil): Key={}", key);
        } else if (valueFromRedis.equals(refreshToken)) {
            log.info("[Redis] SET 직후 GET 검증 성공: Key={}", key);
        } else {
            log.warn("[Redis] SET 직후 GET 값 불일치: Key={}, Value={}", key, valueFromRedis);
        }
    }

    public String getRefreshToken(Long userId) {
        String key = "RT:" + userId.toString();
        return redisTemplate.opsForValue().get(key);
    }

    public boolean deleteRefreshToken(Long userId) {
        String key = "RT:" + userId.toString();
        Boolean result = redisTemplate.delete(key);
        log.info("[Redis] Refresh Token 삭제: Key={}, Result={}", key, result);
        return Boolean.TRUE.equals(result);
    }

    public void addToDenylist(String accessToken, long expirationMillis) {
        String key = "BL:" + accessToken;

        Duration duration = Duration.ofMillis(expirationMillis);

        if (expirationMillis > 0) {
            log.info("[Redis] SET 거부 목록: Key={}, Duration={}s", key, duration.toSeconds());
            redisTemplate.opsForValue().set(key, "logout", duration);
        }
    }

    public boolean isTokenInDenylist(String accessToken) {
        String key = "BL:" + accessToken;
        String value = redisTemplate.opsForValue().get(key);
        return value != null;
    }
}