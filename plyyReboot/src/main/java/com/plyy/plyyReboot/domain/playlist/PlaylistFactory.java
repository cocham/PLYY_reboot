package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.playlist.exception.GenreNotFoundException;
import com.plyy.plyyReboot.domain.playlist.exception.MoodNotFoundException;
import com.plyy.plyyReboot.domain.playlist.exception.SubGenreNotFoundException;
import com.plyy.plyyReboot.domain.preference.genre.Genre;
import com.plyy.plyyReboot.domain.preference.genre.GenreRepository;
import com.plyy.plyyReboot.domain.preference.genre.SubGenre;
import com.plyy.plyyReboot.domain.preference.genre.SubGenreRepository;
import com.plyy.plyyReboot.domain.preference.mood.Mood;
import com.plyy.plyyReboot.domain.preference.mood.MoodRepository;
import com.plyy.plyyReboot.domain.preference.tag.Tag;
import com.plyy.plyyReboot.domain.preference.tag.TagClassifier;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.web.api.playlist.dto.PlaylistCreateRequest;
import com.plyy.plyyReboot.web.api.playlist.dto.TrackCurationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Playlist 생성의 복잡성을 캡슐화하는 팩토리
 * - 생성 로직을 한 곳에 집중
 * - 불변성 보장
 */
@Component
@RequiredArgsConstructor
public class PlaylistFactory {

    private final GenreRepository genreRepository;
    private final SubGenreRepository subGenreRepository;
    private final MoodRepository moodRepository;
    private final TagClassifier tagClassifier;

    public PlayList create(PlaylistCreateRequest request,
                           User curator,
                           List<Track> tracks,
                           String coverImageUrl) {

        PlayList playlist = new PlayList();
        playlist.setTitle(request.title());
        playlist.setCurator(curator);
        playlist.setIntroduction(request.introduction());
        playlist.setThumbnailUrl(coverImageUrl);
        playlist.setSpotifyUrl(request.playlistUrl());

        // 장르 설정
        Genre masterGenre = findMasterGenre(request.masterGenreId());
        playlist.setMasterGenre(masterGenre);

        // 관계 설정
        attachSubGenres(playlist, request.subGenreIds());
        attachMoods(playlist, request.moodIds());
        attachTags(playlist, request.tags());

        // 트랙 추가 (큐레이션 포함)
        attachTracks(playlist, tracks, request.trackCurations());

        return playlist;
    }

    private Genre findMasterGenre(Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException("마스터 장르를 찾을 수 없습니다: " + genreId));
    }

    private void attachSubGenres(PlayList playlist, List<Long> subGenreIds) {
        if (subGenreIds == null || subGenreIds.isEmpty()) {
            return;
        }

        subGenreIds.forEach(id -> {
            SubGenre subGenre = subGenreRepository.findById(id)
                    .orElseThrow(() -> new SubGenreNotFoundException("서브 장르를 찾을 수 없습니다: " + id));
            playlist.addSubGenre(subGenre);
        });
    }

    private void attachMoods(PlayList playlist, List<Long> moodIds) {
        if (moodIds == null || moodIds.isEmpty()) {
            return;
        }

        moodIds.forEach(id -> {
            Mood mood = moodRepository.findById(id)
                    .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다: " + id));
            playlist.addMood(mood);
        });
    }

    private void attachTags(PlayList playlist, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        Set<String> uniqueTags = new HashSet<>(tagNames);
        uniqueTags.stream()
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .forEach(name -> {
                    Tag tag = tagClassifier.classifyAndCreate(name);
                    playlist.addTag(tag);
                });
    }

    private void attachTracks(PlayList playlist,
                              List<Track> tracks,
                              List<TrackCurationRequest> curations) {

        Map<Integer, String> curationMap = buildCurationMap(curations);

        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            String introduction = curationMap.get(i);

            PlaylistTrack playlistTrack = PlaylistTrack.of(track, i + 1, introduction);
            playlist.addTrack(playlistTrack);
        }
    }

    private Map<Integer, String> buildCurationMap(List<TrackCurationRequest> curations) {
        if (curations == null || curations.isEmpty()) {
            return Collections.emptyMap();
        }

        return curations.stream()
                .collect(Collectors.toMap(
                        TrackCurationRequest::trackIndex,
                        TrackCurationRequest::introduction
                ));
    }
}
