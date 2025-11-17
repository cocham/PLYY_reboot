package com.plyy.plyyReboot.config.security.redis.exception;

/**
 * Refresh Token 저장 실패 시 발생하는 예외
 */
public class RefreshTokenStorageException extends BaseRedisException {

    public RefreshTokenStorageException(String message) {
        super(message, RedisErrorCode.REFRESH_TOKEN_STORAGE_FAIL);
    }

    public RefreshTokenStorageException(String message, Throwable cause) {
        super(message, RedisErrorCode.REFRESH_TOKEN_STORAGE_FAIL, cause);
    }

    public RefreshTokenStorageException(Throwable cause) {
        super(RedisErrorCode.REFRESH_TOKEN_STORAGE_FAIL, cause);
    }
}
