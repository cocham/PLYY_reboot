package com.plyy.plyyReboot.config.security.redis;

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
     */
    public static String refreshTokenKey(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }
        return REFRESH_TOKEN_PREFIX + userId;
    }

    /**
     * 토큰 거부 목록(Denylist) 키 생성
     * 형식: BL:{accessToken}
     *
     * @param accessToken 액세스 토큰
     * @return Redis 키
     */
    public static String denylistKey(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken은 비어있을 수 없습니다");
        }
        return DENYLIST_PREFIX + accessToken;
    }

    /**
     * 주어진 키가 Refresh Token 키인지 확인
     */
    public static boolean isRefreshTokenKey(String key) {
        return key != null && key.startsWith(REFRESH_TOKEN_PREFIX);
    }

    /**
     * 주어진 키가 Denylist 키인지 확인
     */
    public static boolean isDenylistKey(String key) {
        return key != null && key.startsWith(DENYLIST_PREFIX);
    }
}
