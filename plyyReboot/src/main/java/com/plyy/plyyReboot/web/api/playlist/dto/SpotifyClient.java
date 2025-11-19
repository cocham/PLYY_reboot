package com.plyy.plyyReboot.web.api.playlist.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
public class SpotifyClient {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;

    public SpotifyClient(WebClient.Builder webClientBuilder,
                         @Value("${spotify.api.url}") String apiUrl,
                         @Value("${spotify.api.auth-url}") String authUrl,
                         @Value("${spotify.client-id}") String clientId,
                         @Value("${spotify.client-secret}") String clientSecret) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private Mono<String> getAccessToken() {
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return WebClient.builder().baseUrl(authUrl).build()
                .post()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyDto.SpotifyTokenResponse.class)
                .map(SpotifyDto.SpotifyTokenResponse::accessToken);
    }

    public Mono<SpotifyDto.SpotifyPlaylistResponse> getPlaylist(String playlistId) {
        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/playlists/{playlistId}")
                                .queryParam("fields", "id,name,description,tracks.items(track(id,name,duration_ms,album(name,images),artists(name)))")
                                .build(playlistId)
                        )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(SpotifyDto.SpotifyPlaylistResponse.class)
                );
    }

    public String extractPlaylistIdFromUrl(String url) {
        try {
            // (예: https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M)
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\?")[0];
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 스포티파이 URL입니다: " + url, e);
        }
    }
}