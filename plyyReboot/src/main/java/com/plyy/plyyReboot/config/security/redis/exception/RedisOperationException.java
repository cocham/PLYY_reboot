package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * 일반적인 Redis 작업 실패 시 발생하는 예외
 */
public class RedisOperationException extends BaseRedisException {

    public RedisOperationException(String message) {
        super(message, RedisErrorCode.REDIS_OPERATION_FAIL);
    }

    public RedisOperationException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_OPERATION_FAIL, cause);
    }

    public RedisOperationException(Throwable cause) {
        super(RedisErrorCode.REDIS_OPERATION_FAIL, cause);
    }
}