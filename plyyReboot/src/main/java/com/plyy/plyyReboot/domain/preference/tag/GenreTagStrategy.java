package com.plyy.plyyReboot.domain.preference.tag;

import com.plyy.plyyReboot.domain.preference.genre.GenreRepository;
import com.plyy.plyyReboot.domain.preference.genre.SubGenreRepository;
import lombok.RequiredArgsConstructor;

/**
 * 장르 기반 태그 전략
 */
@RequiredArgsConstructor
class GenreTagStrategy implements TagTypeStrategy {
    private final GenreRepository genreRepository;
    private final SubGenreRepository subGenreRepository;

    @Override
    public boolean matches(String tagName) {
        return genreRepository.existsByNameContaining(tagName) ||
                subGenreRepository.existsByNameContaining(tagName);
    }

    @Override
    public TagType getType() {
        return TagType.GENRE;
    }
}
