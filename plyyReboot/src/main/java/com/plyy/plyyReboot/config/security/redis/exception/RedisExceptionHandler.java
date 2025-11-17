package com.plyy.plyyReboot.config.security.redis.exception;

import com.plyy.plyyReboot.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Redis 관련 예외 통합 처리
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.plyy.plyyReboot.config.security.redis")
public class RedisExceptionHandler {

    // ========== 연결/네트워크 관련 ==========

    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnection(RedisConnectionException e) {
        log.error("Redis 연결 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(e.getErrorCode(), "일시적인 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    @ExceptionHandler(RedisTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleRedisTimeout(RedisTimeoutException e) {
        log.error("Redis 타임아웃: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(new ErrorResponse(e.getErrorCode(), "요청 처리 시간이 초과되었습니다. 다시 시도해주세요."));
    }

    @ExceptionHandler(RedisConnectionPoolException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnectionPool(RedisConnectionPoolException e) {
        log.error("Redis 커넥션 풀 고갈: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(e.getErrorCode(), "서버가 일시적으로 과부하 상태입니다. 잠시 후 다시 시도해주세요."));
    }

    // ========== 데이터 작업 관련 ==========

    @ExceptionHandler(RedisDataSaveException.class)
    public ResponseEntity<ErrorResponse> handleRedisDataSave(RedisDataSaveException e) {
        log.error("Redis 데이터 저장 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "데이터 저장에 실패했습니다."));
    }

    @ExceptionHandler(RedisDataRetrievalException.class)
    public ResponseEntity<ErrorResponse> handleRedisDataRetrieval(RedisDataRetrievalException e) {
        log.error("Redis 데이터 조회 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "데이터 조회에 실패했습니다."));
    }

    @ExceptionHandler(RedisDataDeleteException.class)
    public ResponseEntity<ErrorResponse> handleRedisDataDelete(RedisDataDeleteException e) {
        log.error("Redis 데이터 삭제 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "데이터 삭제에 실패했습니다."));
    }

    @ExceptionHandler(RedisDataUpdateException.class)
    public ResponseEntity<ErrorResponse> handleRedisDataUpdate(RedisDataUpdateException e) {
        log.error("Redis 데이터 수정 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "데이터 수정에 실패했습니다."));
    }

    @ExceptionHandler(RedisKeyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRedisKeyNotFound(RedisKeyNotFoundException e) {
        log.warn("Redis 키 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorCode(), "요청한 데이터를 찾을 수 없습니다."));
    }

    // ========== 토큰 관련 ==========

    @ExceptionHandler(RefreshTokenStorageException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenStorage(RefreshTokenStorageException e) {
        log.error("Refresh Token 저장 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요."));
    }

    @ExceptionHandler(DenylistAddException.class)
    public ResponseEntity<ErrorResponse> handleDenylistAdd(DenylistAddException e) {
        log.error("Denylist 추가 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "로그아웃 처리 중 오류가 발생했습니다. 다시 시도해주세요."));
    }

    // ========== 키 생성 관련 ==========

    @ExceptionHandler(RedisKeyGenerationException.class)
    public ResponseEntity<ErrorResponse> handleRedisKeyGeneration(RedisKeyGenerationException e) {
        log.error("Redis 키 생성 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), "잘못된 요청입니다."));
    }

    @ExceptionHandler(InvalidRedisKeyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRedisKey(InvalidRedisKeyException e) {
        log.error("유효하지 않은 Redis 키: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), "잘못된 데이터 형식입니다."));
    }

    // ========== 일반 예외 ==========

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ErrorResponse> handleRedisOperation(RedisOperationException e) {
        log.error("Redis 작업 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "작업 처리 중 오류가 발생했습니다."));
    }

    @ExceptionHandler(BaseRedisException.class)
    public ResponseEntity<ErrorResponse> handleBaseRedis(BaseRedisException e) {
        log.error("Redis 오류: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "서버 오류가 발생했습니다."));
    }
}
