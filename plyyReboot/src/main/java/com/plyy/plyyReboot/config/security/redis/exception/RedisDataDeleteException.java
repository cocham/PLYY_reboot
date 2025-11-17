package com.plyy.plyyReboot.config.security.redis.exception;

public class RedisDataDeleteException extends BaseRedisException {

    public RedisDataDeleteException(String message) {
        super(message, RedisErrorCode.REDIS_DATA_DELETE_FAIL);
    }

    public RedisDataDeleteException(String message, Throwable cause) {
        super(message, RedisErrorCode.REDIS_DATA_DELETE_FAIL, cause);
    }

    public RedisDataDeleteException(Throwable cause) {
        super(RedisErrorCode.REDIS_DATA_DELETE_FAIL, cause);
    }
}
