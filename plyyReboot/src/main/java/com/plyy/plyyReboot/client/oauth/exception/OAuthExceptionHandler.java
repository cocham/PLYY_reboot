package com.plyy.plyyReboot.client.oauth.exception;

import com.plyy.plyyReboot.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * OAuth 관련 예외 처리
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.plyy.plyyReboot.client.oauth")
public class OAuthExceptionHandler {

    /**
     * OAuth 속성 누락 예외
     */
    @ExceptionHandler(MissingAttributeException.class)
    public ResponseEntity<ErrorResponse> handleMissingAttribute(
            MissingAttributeException e
    ) {
        log.warn("OAuth authentication failed: {}", e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인에 실패했습니다. 다시 시도해주세요."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 기타 OAuth 인증 예외
     */
    @ExceptionHandler(BaseAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            BaseAuthException e
    ) {
        log.error("OAuth error: {}", e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "인증에 실패했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
}