package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.cache.CachedToken;
import com.plyy.plyyReboot.infrastructure.external.spotify.exception.SpotifyAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SpotifyTokenManager {

    private final SpotifyTokenRepository tokenRepository;
    private final SpotifyAuthClient authClient;

    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private volatile CachedToken localCache;

    private static final int BUFFER_MINUTES = 5;
    private static final int RETRY_ATTEMPTS = 50;
    private static final Duration RETRY_DELAY = Duration.ofMillis(100);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public SpotifyTokenManager(SpotifyTokenRepository tokenRepository,
                               SpotifyAuthClient authClient) {
        this.tokenRepository = tokenRepository;
        this.authClient = authClient;
    }

    public Mono<String> getAccessToken() {
        if (isLocalCacheValid()) {
            return Mono.just(localCache.token());
        }

        return tokenRepository.findToken()
                .filter(this::isTokenValid)
                .doOnNext(this::updateLocalCache)
                .map(CachedToken::token)
                .switchIfEmpty(refreshToken());
    }

    private Mono<String> refreshToken() {
        if (isRefreshing.compareAndSet(false, true)) {
            return performTokenRefresh()
                    .doFinally(signal -> isRefreshing.set(false));
        }
        return waitAndRetry();
    }

    private Mono<String> performTokenRefresh() {
        log.info("Spotify API 토큰 갱신 시작");
        return authClient.requestToken()
                .doOnNext(token -> {
                    updateLocalCache(token);
                    tokenRepository.saveToken(token)
                            .subscribe(
                                    unused -> log.debug("Redis 토큰 저장 완료"),
                                    err -> log.warn("Redis 저장 실패: {}", err.getMessage())
                            );
                })
                .map(CachedToken::token)
                .onErrorResume(e -> {
                    log.error("토큰 갱신 실패: {}", e.getMessage());
                    return Mono.error(new SpotifyAuthenticationException("토큰 갱신 실패", e));
                });
    }

    private Mono<String> waitAndRetry() {
        return Mono.delay(RETRY_DELAY)
                .flatMap(i -> getAccessToken())
                .retry(RETRY_ATTEMPTS)
                .timeout(TIMEOUT)
                .onErrorResume(e -> {
                    log.error("토큰 대기 타임아웃");
                    isRefreshing.set(false);
                    return performTokenRefresh();
                });
    }

    private boolean isLocalCacheValid() {
        return localCache != null && localCache.isValidWithBuffer(BUFFER_MINUTES);
    }

    private boolean isTokenValid(CachedToken token) {
        return token != null && token.isValidWithBuffer(BUFFER_MINUTES);
    }

    private void updateLocalCache(CachedToken token) {
        this.localCache = token;
    }
}
