package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.external.spotify.exception.SpotifyApiException;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.SpotifyPlaylistResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Spotify API 클라이언트
 */
@Slf4j
@Component
public class SpotifyClient {

    private final WebClient webClient;
    private final SpotifyTokenManager tokenManager;

    public SpotifyClient(
            WebClient.Builder webClientBuilder,
            @Value("${spotify.api.url}") String apiUrl,
            SpotifyTokenManager tokenManager
    ) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.tokenManager = tokenManager;
    }

    /**
     * Spotify 플레이리스트 조회
     * 
     * @param playlistId Spotify 플레이리스트 ID
     * @return 플레이리스트 정보
     */
    public Mono<SpotifyPlaylistResponse> getPlaylist(String playlistId) {
        log.debug("Spotify 플레이리스트 조회 요청: {}", playlistId);

        return tokenManager.getAccessToken()
                .flatMap(token -> executePlaylistRequest(playlistId, token))
                .doOnSuccess(response -> 
                    log.info("플레이리스트 조회 성공: id={}, name={}, tracks={}", 
                            response.id(), response.name(), 
                            response.tracks().items().size())
                )
                .doOnError(error -> 
                    log.error("플레이리스트 조회 실패: playlistId={}, error={}", 
                            playlistId, error.getMessage())
                );
    }

    /**
     * 실제 플레이리스트 조회 요청 실행
     */
    private Mono<SpotifyPlaylistResponse> executePlaylistRequest(String playlistId, String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playlists/{playlistId}")
                        .queryParam("fields", buildPlaylistFields())
                        .build(playlistId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(SpotifyPlaylistResponse.class)
                .onErrorMap(e -> new SpotifyApiException(
                        "플레이리스트 조회 실패: " + playlistId, e
                ));
    }

    /**
     * 플레이리스트 필드 지정
     * - 필요한 필드만 요청하여 응답 크기 최소화
     */
    private String buildPlaylistFields() {
        return "id,name,description,images," +
               "tracks.items(track(id,name,duration_ms,album(name,images),artists(name)))";
    }
}
