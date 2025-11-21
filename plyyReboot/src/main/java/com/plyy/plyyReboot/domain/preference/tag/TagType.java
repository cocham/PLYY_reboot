package com.plyy.plyyReboot.domain.preference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagType {
    GENRE("장르"),
    MOOD("무드"),
    CUSTOM("사용자 정의");

    private final String description;
}