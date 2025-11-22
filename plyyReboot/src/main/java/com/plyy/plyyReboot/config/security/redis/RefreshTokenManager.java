package com.plyy.plyyReboot.config.security.redis;

import com.plyy.plyyReboot.config.security.jwt.exception.RefreshTokenMismatchException;
import com.plyy.plyyReboot.config.security.jwt.exception.RefreshTokenNotFoundException;
import com.plyy.plyyReboot.config.security.redis.exception.RedisDataDeleteException;
import com.plyy.plyyReboot.config.security.redis.exception.RedisDataRetrievalException;
import com.plyy.plyyReboot.config.security.redis.exception.RefreshTokenStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Refresh Token 관리
 * - 사용자별 Refresh Token 저장/조회/검증/삭제
 */
@Slf4j
@Component
@RequiredArgsConstructor
class RefreshTokenManager {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TOKEN_DURATION = Duration.ofDays(7);

    public void save(Long userId, String refreshToken) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            redisTemplate.opsForValue().set(key, refreshToken, TOKEN_DURATION);
            log.debug("Refresh Token 저장 성공: userId={}", userId);
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패: userId={}", userId, e);
            throw new RefreshTokenStorageException(
                    "Refresh Token 저장에 실패했습니다: userId=" + userId, e
            );
        }
    }

    public String get(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Refresh Token 조회 실패: userId={}", userId, e);
            throw new RedisDataRetrievalException(
                    "Refresh Token 조회에 실패했습니다: userId=" + userId, e
            );
        }
    }

    public void validate(Long userId, String providedToken) {
        String storedToken = get(userId);

        if (storedToken == null) {
            log.warn("Refresh Token 없음 (세션 만료): userId={}", userId);
            throw new RefreshTokenNotFoundException(
                    "로그인 세션이 만료되었습니다. 다시 로그인해주세요."
            );
        }

        if (!storedToken.equals(providedToken)) {
            log.warn("Refresh Token 불일치 감지: userId={}", userId);
            delete(userId); // 보안 조치
            throw new RefreshTokenMismatchException(
                    "유효하지 않은 토큰입니다. 다시 로그인해주세요."
            );
        }

        log.debug("Refresh Token 검증 성공: userId={}", userId);
    }

    public boolean exists(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Refresh Token 존재 확인 실패: userId={}", userId, e);
            return false;
        }
    }

    public boolean delete(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            Boolean deleted = redisTemplate.delete(key);
            boolean result = Boolean.TRUE.equals(deleted);

            if (result) {
                log.debug("Refresh Token 삭제 성공: userId={}", userId);
            }

            return result;
        } catch (Exception e) {
            log.error("Refresh Token 삭제 실패: userId={}", userId, e);
            throw new RedisDataDeleteException(
                    "Refresh Token 삭제에 실패했습니다: userId=" + userId, e
            );
        }
    }
}
