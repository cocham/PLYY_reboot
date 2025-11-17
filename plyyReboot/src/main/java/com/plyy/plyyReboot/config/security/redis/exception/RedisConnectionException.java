package com.plyy.plyyReboot.config.security.redis.exception;

public class RedisConnectionException extends BaseRedisException {

    public RedisConnectionException(String message) {
        super(message, RedisErrorCode.REDIS_CONNECTION_FAIL);
    }

    public RedisConnectionException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_CONNECTION_FAIL, cause);
    }

    public RedisConnectionException(Throwable cause) {
        super(RedisErrorCode.REDIS_CONNECTION_FAIL, cause);
    }
}