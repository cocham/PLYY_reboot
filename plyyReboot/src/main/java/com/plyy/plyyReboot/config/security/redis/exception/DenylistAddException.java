package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * 토큰 거부 목록 추가 실패 시 발생하는 예외
 */
public class DenylistAddException extends BaseRedisException {

    public DenylistAddException(String message) {
        super(message, RedisErrorCode.DENYLIST_ADD_FAIL);
    }

    public DenylistAddException(String message, Throwable cause) {
        super(message, RedisErrorCode.DENYLIST_ADD_FAIL, cause);
    }

    public DenylistAddException(Throwable cause) {
        super(RedisErrorCode.DENYLIST_ADD_FAIL, cause);
    }
}

