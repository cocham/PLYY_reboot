package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.playlist.port.ExternalPlaylistData.ExternalTrackData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackSynchronizer {

    private final TrackRepository trackRepository;

    @Transactional
    public List<Track> synchronize(List<ExternalTrackData> externalTracks, PlaylistSource source) {
        if (externalTracks.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 외부 트랙 ID 추출
        List<String> spotifyIds = externalTracks.stream()
                .map(ExternalTrackData::externalId)
                .toList();

        // 2. [Bulk Select] DB에서 이미 존재하는 트랙 한 번에 조회
        List<Track> existingTracks = trackRepository.findAllBySpotifyIdIn(spotifyIds);

        // 3. 빠른 검색을 위해 Map으로 변환 (Key: SpotifyId)
        Map<String, Track> trackMap = existingTracks.stream()
                .collect(Collectors.toMap(Track::getSpotifyId, Function.identity()));

        // 4. 결과 리스트 생성 (순서 유지)
        List<Track> resultTracks = new ArrayList<>();

        for (ExternalTrackData data : externalTracks) {
            if (trackMap.containsKey(data.externalId())) {
                resultTracks.add(trackMap.get(data.externalId()));
            } else {
                Track newTrack = createNewTrack(data, source);
                resultTracks.add(trackRepository.save(newTrack));
            }
        }

        return resultTracks;
    }

    private Track createNewTrack(ExternalTrackData data, PlaylistSource source) {
        if (source == PlaylistSource.SPOTIFY) {
            return Track.fromSpotify(
                    data.externalId(), data.title(), data.artist(),
                    data.album(), data.albumArtUrl(), data.durationMs()
            );
        }
        throw new UnsupportedOperationException("지원하지 않는 소스입니다: " + source);
    }
}
