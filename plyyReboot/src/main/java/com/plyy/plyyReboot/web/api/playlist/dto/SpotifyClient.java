package com.plyy.plyyReboot.web.api.playlist.dto;

import com.plyy.plyyReboot.config.security.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SpotifyClient {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final RedisService redisService;

    // 동시성 제어를 위한 플래그
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    // L1 캐시 (메모리)
    private volatile String localAccessToken;
    private volatile LocalDateTime localTokenExpiration;

    public SpotifyClient(WebClient.Builder webClientBuilder,
                         @Value("${spotify.api.url}") String apiUrl,
                         @Value("${spotify.api.auth-url}") String authUrl,
                         @Value("${spotify.client-id}") String clientId,
                         @Value("${spotify.client-secret}") String clientSecret,
                         RedisService redisService) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redisService = redisService;
    }

    /**
     * 토큰 가져오기 (L1 -> L2 -> API 순서)
     */
    private Mono<String> getAccessToken() {
        // 1. L1 캐시 (메모리) 확인 - 5분 버퍼
        if (isLocalTokenValid(5)) {
            return Mono.just(localAccessToken);
        }

        // 2. L2 캐시 (Redis) 확인
        try {
            String redisToken = redisService.getSpotifyToken();
            if (redisToken != null) {
                Long ttl = redisService.getSpotifyTokenTTL();
                if (ttl != null && ttl > 300) {
                    updateLocalToken(redisToken, ttl.intValue());
                    return Mono.just(redisToken);
                }
            }
        } catch (Exception e) {
            log.warn("Redis 토큰 조회 실패 (무시하고 진행): {}", e.getMessage());
        }

        // 3. API 호출 (동시성 제어)
        if (isRefreshing.compareAndSet(false, true)) {
            return requestNewToken()
                    .doFinally(signal -> isRefreshing.set(false));
        } else {
            return Mono.delay(Duration.ofMillis(100))
                    .flatMap(tick -> getAccessToken())
                    .retry(50)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("토큰 획득 타임아웃: {}", e.getMessage());
                        isRefreshing.set(false);
                        return requestNewToken();
                    });
        }
    }

    /**
     * 스포티파이 API에 새 토큰 요청
     */
    private Mono<String> requestNewToken() {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        return WebClient.builder().baseUrl(authUrl).build()
                .post()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyDto.SpotifyTokenResponse.class)
                .map(response -> {
                    String newToken = response.accessToken();
                    int expiresIn = response.expiresIn() > 0 ? response.expiresIn() : 3600;

                    // 1. 메모리 갱신
                    updateLocalToken(newToken, expiresIn);

                    // 2. Redis 저장
                    Mono.fromRunnable(() -> {
                                try {
                                    redisService.saveSpotifyToken(newToken, expiresIn - 100);
                                } catch (Exception e) {
                                    log.warn("Redis 토큰 저장 실패 (메모리만 사용): {}", e.getMessage());
                                }
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();

                    // 3. 토큰 즉시 반환
                    return newToken;
                })
                .onErrorResume(error -> {
                    log.error("Spotify 토큰 발급 실패: {}", error.getMessage());
                    return Mono.error(new RuntimeException("Spotify 인증 실패", error));
                });
    }

    public Mono<SpotifyDto.SpotifyPlaylistResponse> getPlaylist(String playlistId) {
        return getAccessToken()
                .flatMap(token -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/playlists/{playlistId}")
                                .queryParam("fields", "id,name,description,images,tracks.items(track(id,name,duration_ms,album(name,images),artists(name)))")
                                .build(playlistId)
                        )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(SpotifyDto.SpotifyPlaylistResponse.class)
                );
    }

    public String extractPlaylistIdFromUrl(String url) {
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\?")[0];
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 스포티파이 URL입니다: " + url, e);
        }
    }

    private boolean isLocalTokenValid(int bufferMinutes) {
        return localAccessToken != null &&
                localTokenExpiration != null &&
                LocalDateTime.now().plusMinutes(bufferMinutes).isBefore(localTokenExpiration);
    }

    private void updateLocalToken(String token, int expiresInSeconds) {
        this.localAccessToken = token;
        this.localTokenExpiration = LocalDateTime.now()
                .plusSeconds(expiresInSeconds - 60);
    }
}