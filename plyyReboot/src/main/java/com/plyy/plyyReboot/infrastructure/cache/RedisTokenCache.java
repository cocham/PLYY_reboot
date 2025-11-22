package com.plyy.plyyReboot.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis 캐시 전략 (L2)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenCache implements TokenCacheStrategy<CachedToken> {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<CachedToken> get(String key) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                CachedToken token = objectMapper.readValue(json, CachedToken.class);
                if (!token.isExpired()) {
                    return Optional.of(token);
                }
                try { evict(key); } catch (Exception ignored) {}
            }
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패 (API 호출로 대체됨): key={}, msg={}", key, e.getMessage());
        } catch (Exception e) {
            log.error("Redis 캐시 조회 중 예상치 못한 오류: key={}", key, e);
        }

        return Optional.empty();
    }

    @Override
    public void put(String key, CachedToken value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            long ttlSeconds = value.remainingSeconds();

            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds));
            }
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 저장 실패 (연결 오류): key={}, msg={}", key, e.getMessage());
        } catch (Exception e) {
            log.error("Redis 저장 실패 (데이터 오류): key={}", key, e);
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 삭제 실패 (연결 오류): key={}", key);
        } catch (Exception e) {
            log.error("Redis 삭제 실패 (예상치 못한 오류): key={}", key, e);
        }
    }

    @Override
    public boolean contains(String key) {
        return get(key).isPresent();
    }
}
