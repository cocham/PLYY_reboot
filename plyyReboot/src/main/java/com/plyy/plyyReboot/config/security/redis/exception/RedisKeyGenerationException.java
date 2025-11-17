package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 키 생성 시 유효하지 않은 파라미터가 전달될 때 발생하는 예외
 */
public class RedisKeyGenerationException extends BaseRedisException {

    public RedisKeyGenerationException(String message) {
        super(message, RedisErrorCode.REDIS_KEY_GENERATION_FAIL);
    }

    public RedisKeyGenerationException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_KEY_GENERATION_FAIL, cause);
    }
}
