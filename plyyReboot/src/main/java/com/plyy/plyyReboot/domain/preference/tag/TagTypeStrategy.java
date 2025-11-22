package com.plyy.plyyReboot.domain.preference.tag;

/**
 * 태그 타입 결정 전략 인터페이스
 */
interface TagTypeStrategy {
    boolean matches(String tagName);
    TagType getType();
}
