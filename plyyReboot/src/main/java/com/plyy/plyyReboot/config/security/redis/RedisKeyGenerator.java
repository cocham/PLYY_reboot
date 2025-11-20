package com.plyy.plyyReboot.config.security.redis;

import com.plyy.plyyReboot.config.security.redis.exception.InvalidRedisKeyException;
import com.plyy.plyyReboot.config.security.redis.exception.RedisKeyGenerationException;

/**
 * Redis 키 생성을 담당하는 유틸리티 클래스
 * 키 형식을 중앙에서 관리하여 일관성 유지
 */
public final class RedisKeyGenerator {

    private RedisKeyGenerator() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다");
    }

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String DENYLIST_PREFIX = "BL:";

    /**
     * Refresh Token 저장용 키 생성
     * 형식: RT:{userId}
     *
     * @param userId 사용자 ID
     * @return Redis 키
     * @throws RedisKeyGenerationException userId가 null인 경우
     */
    public static String refreshTokenKey(Long userId) {
        if (userId == null) {
            throw new RedisKeyGenerationException("userId는 null일 수 없습니다");
        }

        try {
            return REFRESH_TOKEN_PREFIX + userId;
        } catch (Exception e) {
            throw new RedisKeyGenerationException("Refresh Token 키 생성에 실패했습니다: userId=" + userId, e);
        }
    }

    /**
     * 토큰 거부 목록(Denylist) 키 생성
     * 형식: BL:{accessToken}
     *
     * @param accessToken 액세스 토큰
     * @return Redis 키
     * @throws InvalidRedisKeyException accessToken이 null이거나 비어있는 경우
     */
    public static String denylistKey(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new InvalidRedisKeyException("accessToken은 비어있을 수 없습니다");
        }

        try {
            return DENYLIST_PREFIX + accessToken;
        } catch (Exception e) {
            throw new RedisKeyGenerationException("Denylist 키 생성에 실패했습니다", e);
        }
    }

    // 스포티파이 서버 토큰용 키 접두사
    private static final String SPOTIFY_TOKEN_KEY = "SERVER:SPOTIFY_TOKEN";

    /**
     * 스포티파이 서버 액세스 토큰 키 생성
     * 형식: SERVER:SPOTIFY_TOKEN
     */
    public static String spotifyTokenKey() {
        return SPOTIFY_TOKEN_KEY;
    }

    /**
     * 주어진 키가 Refresh Token 키인지 확인
     *
     * @param key 확인할 키
     * @return Refresh Token 키이면 true
     */
    public static boolean isRefreshTokenKey(String key) {
        return key != null && key.startsWith(REFRESH_TOKEN_PREFIX);
    }

    /**
     * 주어진 키가 Denylist 키인지 확인
     *
     * @param key 확인할 키
     * @return Denylist 키이면 true
     */
    public static boolean isDenylistKey(String key) {
        return key != null && key.startsWith(DENYLIST_PREFIX);
    }

    /**
     * 키 접두사 검증 (내부 사용)
     *
     * @param key 검증할 키
     * @param expectedPrefix 예상되는 접두사
     * @throws InvalidRedisKeyException 키 형식이 올바르지 않은 경우
     */
    private static void validateKeyPrefix(String key, String expectedPrefix) {
        if (key == null || !key.startsWith(expectedPrefix)) {
            throw new InvalidRedisKeyException(
                    String.format("키가 올바른 형식이 아닙니다. 예상 접두사: %s, 실제 키: %s",
                            expectedPrefix, key)
            );
        }
    }
}
