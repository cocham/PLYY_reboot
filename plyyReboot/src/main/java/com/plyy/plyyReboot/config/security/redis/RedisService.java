package com.plyy.plyyReboot.config.security.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis 기반 토큰 관리 서비스
 * Refresh Token 저장 및 Access Token Denylist 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);
    private static final String DENYLIST_VALUE = "denied";

    // ========== Refresh Token 관리 ==========

    /**
     * Refresh Token 저장
     *
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_DURATION);
            log.debug("Refresh Token 저장 성공: userId={}", userId);
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패: userId={}", userId, e);
            throw new RedisOperationException("Refresh Token 저장에 실패했습니다", e);
        }
    }

    /**
     * Refresh Token 조회
     *
     * @param userId 사용자 ID
     * @return Refresh Token (없으면 null)
     */
    public String getRefreshToken(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Refresh Token 조회 실패: userId={}", userId, e);
            return null;
        }
    }

    /**
     * Refresh Token 존재 여부 확인
     */
    public boolean hasRefreshToken(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Refresh Token 존재 확인 실패: userId={}", userId, e);
            return false;
        }
    }

    /**
     * Refresh Token 삭제
     *
     * @param userId 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteRefreshToken(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            Boolean deleted = redisTemplate.delete(key);
            boolean result = Boolean.TRUE.equals(deleted);

            if (result) {
                log.debug("Refresh Token 삭제 성공: userId={}", userId);
            } else {
                log.debug("Refresh Token 없음 (삭제 불필요): userId={}", userId);
            }

            return result;
        } catch (Exception e) {
            log.error("Refresh Token 삭제 실패: userId={}", userId, e);
            return false;
        }
    }

    // ========== Access Token Denylist 관리 ==========

    /**
     * Access Token을 거부 목록에 추가 (로그아웃 시)
     *
     * @param accessToken 거부할 Access Token
     * @param expirationMillis 토큰의 남은 유효 시간 (밀리초)
     */
    public void addToDenylist(String accessToken, long expirationMillis) {
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
            throw new RedisOperationException("토큰 거부 목록 추가에 실패했습니다", e);
        }
    }

    /**
     * 토큰이 거부 목록에 있는지 확인
     *
     * @param accessToken 확인할 Access Token
     * @return 거부 목록에 있으면 true
     */
    public boolean isTokenInDenylist(String accessToken) {
        String key = RedisKeyGenerator.denylistKey(accessToken);

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("거부 목록 확인 실패", e);
            return true;
        }
    }

    /**
     * Redis 작업 실패 시 발생하는 예외
     */
    public static class RedisOperationException extends RuntimeException {
        public RedisOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}