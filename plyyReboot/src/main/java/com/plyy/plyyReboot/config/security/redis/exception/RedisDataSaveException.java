package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 데이터 저장 실패 시 발생하는 예외
 */
public class RedisDataSaveException extends BaseRedisException {

    public RedisDataSaveException(String message) {
        super(message, RedisErrorCode.REDIS_DATA_SAVE_FAIL);
    }

    public RedisDataSaveException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_DATA_SAVE_FAIL, cause);
    }

    public RedisDataSaveException(Throwable cause) {
        super(RedisErrorCode.REDIS_DATA_SAVE_FAIL, cause);
    }
}
