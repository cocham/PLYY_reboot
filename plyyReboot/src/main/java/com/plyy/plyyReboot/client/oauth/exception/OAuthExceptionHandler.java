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
        log.warn("OAuth 인증 실패: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인에 실패했습니다. 다시 시도해주세요."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 속성 타입 불일치 예외
     */
    @ExceptionHandler(InvalidAttributeTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAttributeType(
            InvalidAttributeTypeException e
    ) {
        log.warn("OAuth 속성 타입 불일치 {}: {}", e.getDetailInfo(), e.getMessage(), e);
        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인에 실패했습니다. 인증 정보 형식이 올바르지 않습니다."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 빈 값 예외
     */
    @ExceptionHandler(BlankAttributeException.class)
    public ResponseEntity<ErrorResponse> handleBlankAttribute(
            BlankAttributeException e
    ) {
        log.warn("OAuth 속성이 비어 있음 {}: {}", e.getDetailInfo(), e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인에 실패했습니다. 필수 정보가 비어있습니다."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 응답 구조 불일치 예외
     */
    @ExceptionHandler(InvalidResponseStructureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResponseStructure(
            InvalidResponseStructureException e
    ) {
        log.warn("OAuth 응답 구조가 유효하지 않음 {}: {}", e.getDetailInfo(), e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인에 실패했습니다. 인증 응답 구조가 올바르지 않습니다."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 역할(Role) 정보 오류 예외 처리
     */
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRole(InvalidRoleException e) {
        log.warn("OAuth 역할 정보 오류: {}", e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(
                e.getErrorCode(),
                "소셜 로그인 실패: 사용자 권한 정보를 확인할 수 없습니다."
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(InvalidProviderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProvider(InvalidProviderException e) {
        log.error("유효하지 않은 OAuth 제공자: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), "지원하지 않는 소셜 로그인 방식입니다."));
    }

    @ExceptionHandler(EmailInvalidException.class)
    public ResponseEntity<ErrorResponse> handleEmailInvalid(EmailInvalidException e) {
        log.warn("유효하지 않은 이메일: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), "이메일 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(ProviderIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProviderIdNotFound(ProviderIdNotFoundException e) {
        log.warn("제공자 ID를 찾을 수 없음: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), "소셜 로그인 사용자 ID를 찾을 수 없습니다."));
    }

    @ExceptionHandler(BaseAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(BaseAuthException e) {
        log.error("OAuth 인증 오류: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getErrorCode(), "인증에 실패했습니다."));
    }
}