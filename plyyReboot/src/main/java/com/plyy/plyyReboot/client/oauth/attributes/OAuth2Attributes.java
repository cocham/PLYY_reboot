package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import com.plyy.plyyReboot.client.oauth.exception.MissingAttributeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OAuth2 속성 추상 클래스
 * - 공통 필드: attributes
 * - 공통 검증 로직: validateRequired, getNestedMap
 * - 하위 클래스는 email(), providerId()만 구현
 */
@Slf4j
public abstract class OAuth2Attributes {
    protected final Map<String, Object> attributes;

    protected OAuth2Attributes(Map<String, Object> attributes) {
        if (attributes == null) {
            log.error("OAuth attributes is null");
            throw new MissingAttributeException("인증 정보를 받지 못했습니다.");
        }
        this.attributes = Map.copyOf(attributes);
    }

    public abstract Email email();
    public abstract ProviderId providerId();
    public abstract AuthProvider provider();

    public Map<String, Object> raw() {
        return attributes;
    }

    /**
     * 필수 속성 검증 (null, 타입, 빈 값)
     *
     * @param value 검증할 값
     * @param fieldName 필드명 (로그용)
     * @param expectedType 기대하는 타입
     * @return 검증된 값
     */
    protected <T> T validateRequired(Object value, String fieldName, Class<T> expectedType) {
        if (value == null) {
            log.error("OAuth 속성 누락: provider={}, field={}", provider(), fieldName);
            throw new MissingAttributeException("필수 정보를 제공받지 못했습니다.");
        }

        if (!expectedType.isInstance(value)) {
            log.error("OAuth 속성 타입 불일치: provider={}, field={}, expected={}, actual={}",
                    provider(), fieldName, expectedType.getName(), value.getClass().getName());
            throw new MissingAttributeException("인증 정보 형식이 올바르지 않습니다.");
        }

        T typedValue = expectedType.cast(value);

        if (typedValue instanceof String str && str.isBlank()) {
            log.error("OAuth 속성 값 비어있음: provider={}, field={}", provider(), fieldName);
            throw new MissingAttributeException("필수 정보가 비어있습니다.");
        }

        return typedValue;
    }

    /**
     * 중첩된 Map에서 값 추출 및 검증
     *
     * @param key Map의 키
     * @return 추출된 중첩 Map
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getNestedMap(String key) {
        Object value = attributes.get(key);

        if (value == null) {
            log.error("중첩 맵 누락: provider={}, key={}", provider(), key);
            throw new MissingAttributeException("인증 응답 구조가 올바르지 않습니다.");
        }

        if (!(value instanceof Map)) {
            log.error("중첩 맵 타입 불일치: provider={}, key={}, type={}",
                    provider(), key, value.getClass().getName());
            throw new MissingAttributeException("인증 응답 구조가 올바르지 않습니다.");
        }

        return (Map<String, Object>) value;
    }

    /**
     * 중첩 Map에서 특정 필드 추출 (편의 메서드)
     *
     * @param nestedKey 중첩 Map의 키
     * @param fieldKey 필드 키
     * @param fieldType 필드 타입
     * @return 추출된 값
     */
    protected <T> T getNestedField(String nestedKey, String fieldKey, Class<T> fieldType) {
        Map<String, Object> nestedMap = getNestedMap(nestedKey);
        Object fieldValue = nestedMap.get(fieldKey);
        return validateRequired(fieldValue, nestedKey + "." + fieldKey, fieldType);
    }
}