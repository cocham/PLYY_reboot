package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.cache.CachedToken;
import com.plyy.plyyReboot.infrastructure.external.spotify.exception.SpotifyAuthenticationException;
import com.plyy.plyyReboot.web.api.playlist.dto.SpotifyDto.SpotifyTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SpotifyAuthClient {

    private final WebClient webClient;
    private final SpotifyCredentials credentials;

    public SpotifyAuthClient(
            @Value("${spotify.api.auth-url}") String authUrl,
            @Value("${spotify.client-id}") String clientId,
            @Value("${spotify.client-secret}") String clientSecret
    ) {
        this.webClient = WebClient.builder().baseUrl(authUrl).build();
        this.credentials = new SpotifyCredentials(clientId, clientSecret);
    }

    public Mono<CachedToken> requestToken() {
        return webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials.encodeToBase64())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class)
                .map(response -> {
                    int expiresIn = response.expiresIn() > 0 ? response.expiresIn() : 3600;
                    return CachedToken.of(response.accessToken(), expiresIn);
                })
                .onErrorMap(e -> {
                    return new SpotifyAuthenticationException("Spotify 인증 요청 실패", e);
                });
    }
}
