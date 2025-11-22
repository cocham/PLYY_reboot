package com.plyy.plyyReboot.web.api.playlist;

import com.plyy.plyyReboot.domain.playlist.*;
import com.plyy.plyyReboot.domain.playlist.port.*;
import com.plyy.plyyReboot.domain.preference.genre.Genre;
import com.plyy.plyyReboot.domain.preference.genre.GenreRepository;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.web.api.playlist.dto.PlaylistCreateRequest;
import com.plyy.plyyReboot.web.api.playlist.dto.TrackCurationRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlayListRepository playlistRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    private final TrackSynchronizer trackSynchronizer;
    private final ExternalPlaylistPortFactory portFactory;

    @Transactional
    public Long createPlaylist(PlaylistCreateRequest request, MultipartFile coverImage, Long curatorId) {

        // 1. 큐레이터 & 장르 조회
        User curator = userRepository.findById(curatorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Genre masterGenre = genreRepository.findById(request.masterGenreId())
                .orElseThrow(() -> new IllegalArgumentException("장르를 찾을 수 없습니다."));

        // 2. 외부 데이터 가져오기
        PlaylistSource sourceEnum = PlaylistSource.valueOf(request.source().toUpperCase());

        // of(식별값(URL), 소스(Enum)) 순서로 전달
        PlaylistId externalId = PlaylistId.fromUrl(request.playlistUrl(), sourceEnum);

        ExternalPlaylistPort port = portFactory.getPort(externalId);

        ExternalPlaylistData externalData = port.fetchPlaylist(externalId).block();

        // 3. 트랙 동기화
        List<Track> tracks = trackSynchronizer.synchronize(
                externalData.tracks(),
                externalId.getSource()
        );

        // 4. 엔티티 생성 (Builder 사용)
        PlayList playlist = PlayList.builder()
                .title(request.title())
                .curator(curator)
                .masterGenre(masterGenre)
                .introduction(request.introduction())
                .spotifyUrl(request.playlistUrl())
                .thumbnailUrl(externalData.thumbnailUrl())
                .build();

        // 5. 큐레이션 리스트를 Map으로 변환
        Map<Integer, String> curationMap = new HashMap<>();
        if (request.trackCurations() != null) {
            curationMap = request.trackCurations().stream()
                    .collect(Collectors.toMap(
                            TrackCurationRequest::trackIndex,
                            TrackCurationRequest::introduction
                    ));
        }

        // 6. 트랙 추가
        for (int i = 0; i < tracks.size(); i++) {
            int trackOrder = i + 1;
            String curation = curationMap.get(trackOrder);
            playlist.addTrack(tracks.get(i), trackOrder, curation);
        }

        return playlistRepository.save(playlist).getId();
    }
}
