package com.plyy.plyyReboot.config.security.redis.exception;

import com.plyy.plyyReboot.exception.ErrorCodeInterface;

/**
 * Redis 관련 에러 코드
 */
public enum RedisErrorCode implements ErrorCodeInterface {
    // 연결/네트워크 관련 (REDIS_1XX)
    REDIS_CONNECTION_FAIL("REDIS_101", "Redis 서버 연결에 실패했습니다."),
    REDIS_TIMEOUT("REDIS_102", "Redis 작업 시간이 초과되었습니다."),
    REDIS_CONNECTION_POOL_EXHAUSTED("REDIS_103", "Redis 연결 풀이 고갈되었습니다."),

    // 데이터 작업 관련 (REDIS_2XX)
    REDIS_DATA_SAVE_FAIL("REDIS_201", "Redis 데이터 저장에 실패했습니다."),
    REDIS_DATA_RETRIEVAL_FAIL("REDIS_202", "Redis 데이터 조회에 실패했습니다."),
    REDIS_DATA_DELETE_FAIL("REDIS_203", "Redis 데이터 삭제에 실패했습니다."),
    REDIS_DATA_UPDATE_FAIL("REDIS_204", "Redis 데이터 수정에 실패했습니다."),
    REDIS_KEY_NOT_FOUND("REDIS_205", "Redis에서 키를 찾을 수 없습니다."),

    // 토큰 관련 (REDIS_3XX)
    REFRESH_TOKEN_STORAGE_FAIL("REDIS_301", "Refresh Token 저장에 실패했습니다."),
    DENYLIST_ADD_FAIL("REDIS_302", "토큰 거부 목록 추가에 실패했습니다."),

    // 키 생성 관련 (REDIS_4XX)
    REDIS_KEY_GENERATION_FAIL("REDIS_401", "Redis 키 생성에 실패했습니다."),
    INVALID_REDIS_KEY("REDIS_402", "유효하지 않은 Redis 키입니다."),

    // 일반 작업 실패
    REDIS_OPERATION_FAIL("REDIS_999", "Redis 작업에 실패했습니다.");

    private final String code;
    private final String message;

    RedisErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}