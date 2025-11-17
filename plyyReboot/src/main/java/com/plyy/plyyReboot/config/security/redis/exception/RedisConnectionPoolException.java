package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 커넥션 풀 고갈 시 발생하는 예외
 */
class RedisConnectionPoolException extends BaseRedisException {

    public RedisConnectionPoolException(String message) {
        super(message, RedisErrorCode.REDIS_CONNECTION_POOL_EXHAUSTED);
    }

    public RedisConnectionPoolException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_CONNECTION_POOL_EXHAUSTED, cause);
    }

    public RedisConnectionPoolException(Throwable cause) {
        super(RedisErrorCode.REDIS_CONNECTION_POOL_EXHAUSTED, cause);
    }
}
