package com.plyy.plyyReboot.web.api.playlist;

import com.plyy.plyyReboot.domain.playlist.*;
import com.plyy.plyyReboot.domain.preference.*;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.*;
import com.plyy.plyyReboot.web.api.playlist.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final SpotifyClient spotifyClient;
    private final PlayListRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final MoodRepository moodRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final SubGenreRepository subGenreRepository;

    // (S3 파일 업로드 서비스 주입)
    // private final FileUploadService fileUploadService;

    @Transactional
    public Long createPlaylist(PlaylistCreateRequest request, MultipartFile coverImage, Long curatorId) {

        // 0. (파일 업로드 처리)
        // String coverImageUrl = fileUploadService.upload(coverImage, "playlist-covers");
        String coverImageUrl = null;

        if (coverImage != null && !coverImage.isEmpty()) {
            // coverImageUrl = fileUploadService.upload(coverImage, "playlist-covers");
            coverImageUrl = "https://s3.bucket.../uploaded_image.jpg"; // (임시)
        }

        // 1. 큐레이터(User) 조회
        User curator = userRepository.findById(curatorId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. 마스터 장르 조회 (필수)
        Genre masterGenre = genreRepository.findById(request.masterGenreId())
                .orElseThrow(() -> new RuntimeException("마스터 장르를 찾을 수 없습니다."));

        // 3. Spotify API 호출
        if (!"spotify".equals(request.source())) {
            throw new IllegalArgumentException("현재 스포티파이만 지원합니다.");
        }

        String playlistId = spotifyClient.extractPlaylistIdFromUrl(request.playlistUrl());
        SpotifyPlaylistResponse spotifyPlaylist = spotifyClient.getPlaylist(playlistId).block();

        if (spotifyPlaylist == null || spotifyPlaylist.tracks() == null) {
            throw new RuntimeException("스포티파이 플레이리스트 정보를 가져올 수 없습니다.");
        }

        // 4. 트랙 정보(Track 엔티티) 저장
        List<Track> tracks = spotifyPlaylist.tracks().items().stream()
                .map(item -> findOrCreateTrack(item.track()))
                .toList();

        // 5. 플레이리스트(Playlist 엔티티) 생성
        PlayList playlist = new PlayList();
        playlist.setTitle(request.title());
        playlist.setCurator(curator);
        playlist.setIntroduction(request.introduction());
        playlist.setThumbnailUrl(coverImageUrl);
        playlist.setSpotifyUrl(request.playlistUrl());

        // 장르 설정
        playlist.setMasterGenre(masterGenre);

        // 집계 데이터
        playlist.setTotalTrackCount(tracks.size());
        playlist.setTotalDurationMs(tracks.stream().mapToLong(Track::getDurationMs).sum());

        // 6. 연관관계 설정 (SubGenre, Mood, Tag)

        // 6-1. SubGenre (선택)
        if (request.subGenreIds() != null) {
            request.subGenreIds().forEach(subGenreId -> {
                SubGenre subGenre = subGenreRepository.findById(subGenreId)
                        .orElseThrow(() -> new RuntimeException("서브 장르를 찾을 수 없습니다: " + subGenreId));
                playlist.addSubGenre(subGenre);
            });
        }

        // 6-2. Mood (선택)
        if (request.moodIds() != null) {
            request.moodIds().forEach(moodId -> {
                Mood mood = moodRepository.findById(moodId)
                        .orElseThrow(() -> new RuntimeException("무드를 찾을 수 없습니다: " + moodId));
                playlist.addMood(mood);
            });
        }

        // 6-3. Tag (선택)
        if (request.tags() != null) {
            Set<String> uniqueTags = new HashSet<>(request.tags());

            uniqueTags.forEach(tagName -> {
                String cleanName = tagName.trim();
                if (cleanName.isEmpty()) return;

                TagType type = determineTagType(cleanName);
                Tag tag = findOrCreateTag(cleanName, type);
                playlist.addTag(tag);
            });
        }

        // 7. PlaylistTrack (큐레이션) 저장
        Map<Integer, String> curationMap;
        if (request.trackCurations() != null) {
            curationMap = request.trackCurations().stream()
                    .collect(Collectors.toMap(TrackCurationRequest::trackIndex, TrackCurationRequest::introduction));
        } else {
            curationMap = Collections.emptyMap();
        }

        AtomicInteger orderSequence = new AtomicInteger(1);
        tracks.forEach(track -> {
            int currentOrder = orderSequence.getAndIncrement();

            PlaylistTrack playlistTrack = new PlaylistTrack();
            playlistTrack.setTrack(track);

            playlistTrack.setTrackOrder(currentOrder);

            String intro = curationMap.get(currentOrder - 1);
            playlistTrack.setTrackIntroduction(intro);

            playlist.addTrack(playlistTrack);
        });

        PlayList savedPlaylist = playlistRepository.save(playlist);

        return savedPlaylist.getId();
    }

    /**
     * 스포티파이 트랙 정보를 기반으로 DB에서 트랙을 찾거나, 없으면 새로 생성
     *
     * [중복 체크 정책]
     * - 오직 '스포티파이 ID'가 일치할 때만 같은 곡으로 식별함.
     * - 제목이나 가수가 같더라도 ID가 없거나 다르면(예: 라이브 버전, 리믹스 버전),
     * 별개의 곡으로 취급하여 새로 저장
     */
    private Track findOrCreateTrack(SpotifyTrack spotifyTrack) {
        return trackRepository.findBySpotifyId(spotifyTrack.id())
            .orElseGet(() -> {
                Track newTrack = new Track();
                newTrack.setSpotifyId(spotifyTrack.id());
                newTrack.setTitle(spotifyTrack.name());
                newTrack.setArtist(makeArtistString(spotifyTrack.artists()));
                newTrack.setDurationMs(spotifyTrack.durationMs());

                if (spotifyTrack.album() != null) {
                    newTrack.setAlbum(spotifyTrack.album().name());
                    if (spotifyTrack.album().images() != null && !spotifyTrack.album().images().isEmpty()) {
                        newTrack.setAlbumArtUrl(spotifyTrack.album().images().get(0).url());
                    }
                }

                return trackRepository.save(newTrack);
            });
    }

    /**
     * 태그 문자열을 분석하여 타입을 결정하는 로직
     * 순서: 장르(상/하위) -> 무드 -> 커스텀
     */
    private TagType determineTagType(String tagName) {
        if (tagName.length() < 1) {
            return TagType.CUSTOM;
        }

        if (genreRepository.existsByNameContaining(tagName) ||
                subGenreRepository.existsByNameContaining(tagName)) {
            return TagType.GENRE;
        }

        if (moodRepository.existsByNameContaining(tagName)) {
            return TagType.MOOD;
        }

        return TagType.CUSTOM;
    }

    /**
     * 태그 조회 혹은 생성
     */
    private Tag findOrCreateTag(String tagName, TagType type) {
        return tagRepository.findByNameAndType(tagName, type)
                .orElseGet(() -> tagRepository.save(new Tag(tagName, type)));
    }

    /**
     * 아티스트 문자열 생성
     */
    private String makeArtistString(List<SpotifyArtist> artists) {
        return artists.stream()
                .map(SpotifyArtist::name)
                .collect(Collectors.joining(", "));
    }
}