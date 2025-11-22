package com.plyy.plyyReboot.config.security.redis;

import com.plyy.plyyReboot.config.security.redis.exception.DenylistAddException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Token Denylist 관리
 * - 로그아웃된 Access Token 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
class TokenDenylistManager {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DENYLIST_VALUE = "denied";

    public void add(String accessToken, long expirationMillis) {
        if (expirationMillis <= 0) {
            log.debug("만료된 토큰은 거부 목록에 추가하지 않음");
            return;
        }

        String key = RedisKeyGenerator.denylistKey(accessToken);
        Duration duration = Duration.ofMillis(expirationMillis);

        try {
            redisTemplate.opsForValue().set(key, DENYLIST_VALUE, duration);
            log.debug("토큰 거부 목록 추가: 만료={}초", duration.toSeconds());
        } catch (Exception e) {
            log.error("토큰 거부 목록 추가 실패", e);
            throw new DenylistAddException("토큰 거부 목록 추가에 실패했습니다", e);
        }
    }

    public boolean contains(String accessToken) {
        String key = RedisKeyGenerator.denylistKey(accessToken);

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("거부 목록 확인 실패", e);
            return true;
        }
    }
}
