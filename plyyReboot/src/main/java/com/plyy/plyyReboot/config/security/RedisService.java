package com.plyy.plyyReboot.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // [ 1. Log import 추가 ]
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Slf4j // [ 2. @Slf4j 추가 ]
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);

    // ▼▼▼▼▼ [ 3. 이 메서드를 통째로 교체 ] ▼▼▼▼▼
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "RT:" + userId.toString();

        log.info("[Redis] SET 시도: Key={}, Value={}", key, refreshToken);

        // 1. 저장 (SET)
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_DURATION);

        // 2. 저장 직후, 방금 쓴 키로 바로 조회 (GET)
        String valueFromRedis = redisTemplate.opsForValue().get(key);

        // 3. 로그로 검증
        if (valueFromRedis == null) {
            log.error("[Redis] SET 직후 GET 실패! (nil): Key={}", key);
            // (이 로그가 뜬다면, set이 에러 없이 실패한 것임)
        } else if (valueFromRedis.equals(refreshToken)) {
            log.info("[Redis] SET 직후 GET 검증 성공: Key={}", key);
        } else {
            log.warn("[Redis] SET 직후 GET 값 불일치: Key={}, Value={}", key, valueFromRedis);
        }
    }
    // ▲▲▲▲▲ [ "쓰기"와 "읽기"를 동시에 수행 ] ▲▲▲▲▲

    // --- (getRefreshToken, deleteRefreshToken은 일단 그대로 둡니다) ---
    public String getRefreshToken(Long userId) {
        String key = "RT:" + userId.toString();
        return redisTemplate.opsForValue().get(key);
    }

    public boolean deleteRefreshToken(Long userId) {
        String key = "RT:" + userId.toString();
        Boolean result = redisTemplate.delete(key);
        return Boolean.TRUE.equals(result);
    }
}