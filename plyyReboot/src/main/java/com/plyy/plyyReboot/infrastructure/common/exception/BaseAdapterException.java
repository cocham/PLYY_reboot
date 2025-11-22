package com.plyy.plyyReboot.infrastructure.common.exception;

import lombok.Getter;

/**
 * 인프라 스트럭처(Adapter) 계층의 최상위 예외
 * - Spotify, YouTube 등 모든 외부 연동 예외는 이 클래스를 상속받음
 */
@Getter
public abstract class BaseAdapterException extends RuntimeException {

    public BaseAdapterException(String message) {
        super(message);
    }

    public BaseAdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}