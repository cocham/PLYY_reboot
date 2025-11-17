package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 키에 사용할 수 없는 값이 전달될 때 발생하는 예외
 */
public class InvalidRedisKeyException extends BaseRedisException {

    public InvalidRedisKeyException(String message) {
        super(message, RedisErrorCode.INVALID_REDIS_KEY);
    }

    public InvalidRedisKeyException(String message, Throwable cause) {
        super(message, RedisErrorCode.INVALID_REDIS_KEY, cause);
    }
}
