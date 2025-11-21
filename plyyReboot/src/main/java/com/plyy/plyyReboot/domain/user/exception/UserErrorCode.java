package com.plyy.plyyReboot.domain.user.exception;

import com.plyy.plyyReboot.exception.ErrorCodeInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCodeInterface {

    USER_NOT_FOUND("USER_001", "해당 사용자를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
