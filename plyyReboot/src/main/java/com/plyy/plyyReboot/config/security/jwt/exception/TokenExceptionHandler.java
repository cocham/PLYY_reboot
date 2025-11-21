package com.plyy.plyyReboot.config.security.jwt.exception;

import com.plyy.plyyReboot.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 토큰 관련 예외 통합 처리
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.plyy.plyyReboot.config.security")
public class TokenExceptionHandler {

    @ExceptionHandler(TokenIssuanceException.class)
    public ResponseEntity<ErrorResponse> handleTokenIssuance(TokenIssuanceException e) {
        log.error("토큰 발급 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "로그인 처리 중 오류가 발생했습니다."));
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefresh(TokenRefreshException e) {
        log.warn("토큰 갱신 실패: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFound(RefreshTokenNotFoundException e) {
        log.warn("Refresh Token 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), "로그인 세션이 만료되었습니다. 다시 로그인해주세요."));
    }

    @ExceptionHandler(RefreshTokenMismatchException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenMismatch(RefreshTokenMismatchException e) {
        log.warn("Refresh Token 불일치: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), "유효하지 않은 토큰입니다. 다시 로그인해주세요."));
    }

    @ExceptionHandler(TokenRevocationException.class)
    public ResponseEntity<ErrorResponse> handleTokenRevocation(TokenRevocationException e) {
        log.error("토큰 무효화 실패: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getErrorCode(), "로그아웃 처리 중 오류가 발생했습니다."));
    }

    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingAuthorizationHeader(
            MissingAuthorizationHeaderException e
    ) {
        log.debug("Authorization 헤더 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        e.getErrorCode(),
                        "인증이 필요합니다. Authorization 헤더를 포함해주세요."
                ));
    }

    @ExceptionHandler(BaseTokenException.class)
    public ResponseEntity<ErrorResponse> handleBaseToken(BaseTokenException e) {
        log.error("토큰 오류: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), "인증 처리 중 오류가 발생했습니다."));
    }
}
