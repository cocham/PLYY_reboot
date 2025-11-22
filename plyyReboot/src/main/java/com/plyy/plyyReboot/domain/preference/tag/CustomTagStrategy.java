package com.plyy.plyyReboot.domain.preference.tag;

/**
 * 커스텀 태그 전략 (기본)
 */
class CustomTagStrategy implements TagTypeStrategy {
    @Override
    public boolean matches(String tagName) {
        return true;
    }

    @Override
    public TagType getType() {
        return TagType.CUSTOM;
    }
}
