package com.plyy.plyyReboot.domain.preference.tag;

import com.plyy.plyyReboot.domain.preference.genre.GenreRepository;
import com.plyy.plyyReboot.domain.preference.mood.MoodRepository;
import com.plyy.plyyReboot.domain.preference.genre.SubGenreRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 태그 분류 및 생성 전략
 * - 태그의 타입을 판단하는 로직을 캡슐화
 */
@Component
public class TagClassifier {

    private final TagRepository tagRepository;
    private final List<TagTypeStrategy> strategies;

    public TagClassifier(TagRepository tagRepository,
                         GenreRepository genreRepository,
                         SubGenreRepository subGenreRepository,
                         MoodRepository moodRepository) {
        this.tagRepository = tagRepository;
        this.strategies = List.of(
                new GenreTagStrategy(genreRepository, subGenreRepository),
                new MoodTagStrategy(moodRepository),
                new CustomTagStrategy()
        );
    }

    public Tag classifyAndCreate(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> createNewTag(tagName));
    }

    private Tag createNewTag(String tagName) {
        TagType type = determineType(tagName);
        Tag newTag = new Tag(tagName, type);
        return tagRepository.save(newTag);
    }

    private TagType determineType(String tagName) {
        for (TagTypeStrategy strategy : strategies) {
            if (strategy.matches(tagName)) {
                return strategy.getType();
            }
        }
        return TagType.CUSTOM; // 기본값
    }
}
