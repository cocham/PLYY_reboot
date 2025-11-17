package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 작업 타임아웃 시 발생하는 예외
 */
class RedisTimeoutException extends BaseRedisException {

    public RedisTimeoutException(String message) {
        super(message, RedisErrorCode.REDIS_TIMEOUT);
    }

    public RedisTimeoutException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_TIMEOUT, cause);
    }

    public RedisTimeoutException(Throwable cause) {
        super(RedisErrorCode.REDIS_TIMEOUT, cause);
    }
}
