package com.plyy.plyyReboot.client.oauth.exception;

public class MissingAttributeException extends BaseAuthException {

    /**
     * 전체 정보를 포함하는 생성자
     *
     * @param userMessage 사용자용 메시지 (API 응답)
     */
    public MissingAttributeException(String userMessage) {
        super(userMessage, AuthErrorCode.MISSING_ATTRIBUTE);
    }

}
