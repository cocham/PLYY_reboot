package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Redis 데이터 조회 실패 시 발생하는 예외
 */
public class RedisDataRetrievalException extends BaseRedisException {

    public RedisDataRetrievalException(String message) {
        super(message, RedisErrorCode.REDIS_DATA_RETRIEVAL_FAIL);
    }

    public RedisDataRetrievalException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_DATA_RETRIEVAL_FAIL, cause);
    }

    public RedisDataRetrievalException(Throwable cause) {
        super(RedisErrorCode.REDIS_DATA_RETRIEVAL_FAIL, cause);
    }
}