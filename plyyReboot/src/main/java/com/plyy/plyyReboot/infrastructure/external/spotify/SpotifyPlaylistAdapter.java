package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.domain.playlist.PlaylistId;
import com.plyy.plyyReboot.domain.playlist.PlaylistSource;
import com.plyy.plyyReboot.domain.playlist.exception.InvalidPlaylistIdException;
import com.plyy.plyyReboot.domain.playlist.exception.PlaylistFetchFailureException;
import com.plyy.plyyReboot.domain.playlist.port.ExternalPlaylistData;
import com.plyy.plyyReboot.domain.playlist.port.ExternalPlaylistPort;
import com.plyy.plyyReboot.infrastructure.external.spotify.exception.SpotifyApiException; // ✅ 이제 사용합니다!
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.SpotifyTrack;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.SpotifyArtist;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.SpotifyImage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SpotifyPlaylistAdapter implements ExternalPlaylistPort {

    private final SpotifyClient spotifyClient;

    @Override
    public PlaylistSource getSupportedSource() {
        return PlaylistSource.SPOTIFY;
    }

    @Override
    public Mono<ExternalPlaylistData> fetchPlaylist(PlaylistId playlistId) {
        return spotifyClient.getPlaylist(playlistId.getValue())
                .map(this::toExternalPlaylistData)
                .onErrorMap(e -> {
                    Throwable cause = e;

                    if (e instanceof SpotifyApiException) {
                        cause = e.getCause();
                    }

                    if (cause instanceof WebClientResponseException.NotFound) {
                        return new InvalidPlaylistIdException(playlistId.getValue());
                    }

                    return new PlaylistFetchFailureException(playlistId.getValue(), e);
                });
    }

    private ExternalPlaylistData toExternalPlaylistData(SpotifyDto.SpotifyPlaylistResponse response) {
        String thumbnailUrl = extractThumbnailUrl(response.images());

        List<ExternalPlaylistData.ExternalTrackData> tracks = response.tracks().items().stream()
                .map(item -> toExternalTrackData(item.track()))
                .collect(Collectors.toList());

        return new ExternalPlaylistData(
                response.id(),
                response.name(),
                response.description(),
                thumbnailUrl,
                tracks
        );
    }

    private ExternalPlaylistData.ExternalTrackData toExternalTrackData(SpotifyTrack track) {
        String artist = track.artists().stream()
                .map(SpotifyArtist::name)
                .collect(Collectors.joining(", "));

        String albumArtUrl = null;
        String albumName = null;

        if (track.album() != null) {
            albumName = track.album().name();
            albumArtUrl = extractThumbnailUrl(track.album().images());
        }

        return new ExternalPlaylistData.ExternalTrackData(
                track.id(),
                track.name(),
                artist,
                albumName,
                albumArtUrl,
                track.durationMs()
        );
    }

    private String extractThumbnailUrl(List<SpotifyImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0).url();
    }
}
