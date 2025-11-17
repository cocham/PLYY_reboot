package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 키를 찾을 수 없을 때 발생하는 예외
 */
class RedisKeyNotFoundException extends BaseRedisException {

    public RedisKeyNotFoundException(String message) {
        super(message, RedisErrorCode.REDIS_KEY_NOT_FOUND);
    }

    public RedisKeyNotFoundException() {
        super(RedisErrorCode.REDIS_KEY_NOT_FOUND);
    }
}