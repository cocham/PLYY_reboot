package com.plyy.plyyReboot.domain.preference.tag;

import com.plyy.plyyReboot.domain.preference.mood.MoodRepository;
import lombok.RequiredArgsConstructor;

/**
 * 무드 기반 태그 전략
 */
@RequiredArgsConstructor
class MoodTagStrategy implements TagTypeStrategy {
    private final MoodRepository moodRepository;

    @Override
    public boolean matches(String tagName) {
        return moodRepository.existsByNameContaining(tagName);
    }

    @Override
    public TagType getType() {
        return TagType.MOOD;
    }
}
