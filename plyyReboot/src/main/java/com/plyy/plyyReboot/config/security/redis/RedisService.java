package com.plyy.plyyReboot.config.security.redis;

import com.plyy.plyyReboot.config.security.jwt.exception.RefreshTokenMismatchException;
import com.plyy.plyyReboot.config.security.jwt.exception.RefreshTokenNotFoundException;
import com.plyy.plyyReboot.config.security.redis.exception.DenylistAddException;
import com.plyy.plyyReboot.config.security.redis.exception.RedisDataDeleteException;
import com.plyy.plyyReboot.config.security.redis.exception.RedisDataRetrievalException;
import com.plyy.plyyReboot.config.security.redis.exception.RefreshTokenStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
     * @throws RefreshTokenStorageException 저장 실패 시
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_DURATION);
            log.debug("Refresh Token 저장 성공: userId={}", userId);
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패: userId={}", userId, e);
            throw new RefreshTokenStorageException("Refresh Token 저장에 실패했습니다: userId=" + userId, e);
        }
    }

    /**
     * Refresh Token 조회
     *
     * @param userId 사용자 ID
     * @return Refresh Token (없으면 null)
     * @throws RedisDataRetrievalException 조회 실패 시 (null 반환 아닌 예외 상황)
     */
    public String getRefreshToken(Long userId) {
        String key = RedisKeyGenerator.refreshTokenKey(userId);

        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Refresh Token 조회 실패: userId={}", userId, e);
            throw new RedisDataRetrievalException("Refresh Token 조회에 실패했습니다: userId=" + userId, e);
        }
    }

    /**
     * Refresh Token 검증
     * TokenService의 갱신 로직 일부를 옮겨받아,
     * 토큰 조회, 비교, 불일치 시 삭제(보안 조치)를 한 번에 처리
     *
     * @param userId 사용자 ID
     * @param providedToken 사용자가 제공한 Refresh Token
     * @throws RefreshTokenNotFoundException Redis에 토큰이 없음 (세션 만료)
     * @throws RefreshTokenMismatchException 제공된 토큰과 저장된 토큰이 불일치 (탈취 의심)
     * @throws RedisDataRetrievalException 조회 중 Redis 오류 발생
     * @throws RedisDataDeleteException 삭제 중 Redis 오류 발생
     */
    public void validateRefreshToken(Long userId, String providedToken) {
        String storedToken = getRefreshToken(userId);

        if (storedToken == null) {
            log.warn("Refresh Token 검증 실패: 저장된 토큰 없음 (세션 만료), userId={}", userId);
            throw new RefreshTokenNotFoundException("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
        }

        if (!storedToken.equals(providedToken)) {
            log.warn("Refresh Token 불일치 감지 (보안 조치): userId={}", userId);
            deleteRefreshToken(userId);
            throw new RefreshTokenMismatchException("유효하지 않은 토큰입니다. 다시 로그인해주세요.");
        }

        log.debug("Refresh Token 검증 성공: userId={}", userId);
    }

    /**
     * Refresh Token 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 존재하면 true
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
     * @throws RedisDataDeleteException 삭제 실패 시
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
            throw new RedisDataDeleteException("Refresh Token 삭제에 실패했습니다: userId=" + userId, e);
        }
    }

    /**
     * 스포티파이 액세스 토큰 저장
     *
     * @param accessToken 스포티파이에서 받은 토큰
     * @param ttlSeconds  유효 시간 (초 단위). 3500초 설정
     */
    public void saveSpotifyToken(String accessToken, long ttlSeconds) {
        String key = RedisKeyGenerator.spotifyTokenKey();
        try {
            redisTemplate.opsForValue().set(key, accessToken, Duration.ofSeconds(ttlSeconds));
            log.debug("스포티파이 서버 토큰 저장 완료. TTL: {}초", ttlSeconds);
        } catch (Exception e) {
            log.error("스포티파이 토큰 저장 실패 (Redis 오류)", e);
        }
    }

    /**
     * 캐시된 스포티파이 액세스 토큰 조회
     *
     * @return 토큰 문자열 (없거나 만료됐으면 null)
     */
    public String getSpotifyToken() {
        String key = RedisKeyGenerator.spotifyTokenKey();
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("스포티파이 토큰 조회 실패 (Redis 오류)", e);
            return null;
        }
    }

    public Long getSpotifyTokenTTL() {
        String key = RedisKeyGenerator.spotifyTokenKey();
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("스포티파이 토큰 TTL 조회 실패", e);
            return null;
        }
    }

    // ========== Access Token Denylist 관리 ==========

    /**
     * Access Token을 거부 목록에 추가 (로그아웃 시)
     *
     * @param accessToken 거부할 Access Token
     * @param expirationMillis 토큰의 남은 유효 시간 (밀리초)
     * @throws DenylistAddException Denylist 추가 실패 시
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
            throw new DenylistAddException("토큰 거부 목록 추가에 실패했습니다", e);
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
}