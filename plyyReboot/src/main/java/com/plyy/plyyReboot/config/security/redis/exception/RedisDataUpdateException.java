package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 데이터 수정 실패 시 발생하는 예외
 */
class RedisDataUpdateException extends BaseRedisException {

    public RedisDataUpdateException(String message) {
        super(message, RedisErrorCode.REDIS_DATA_UPDATE_FAIL);
    }

    public RedisDataUpdateException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_DATA_UPDATE_FAIL, cause);
    }

    public RedisDataUpdateException(Throwable cause) {
        super(RedisErrorCode.REDIS_DATA_UPDATE_FAIL, cause);
    }
}