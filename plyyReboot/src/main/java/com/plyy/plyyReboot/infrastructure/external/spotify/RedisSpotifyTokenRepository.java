package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.cache.CachedToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
class RedisSpotifyTokenRepository implements SpotifyTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_KEY = "SERVER:SPOTIFY_TOKEN";

    @Override
    public Mono<CachedToken> findToken() {
        return Mono.fromCallable(() -> {
                    String token = redisTemplate.opsForValue().get(TOKEN_KEY);
                    if (token == null) return null;

                    Long ttl = redisTemplate.getExpire(TOKEN_KEY, TimeUnit.SECONDS);
                    if (ttl == null || ttl <= 0) return null;

                    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(ttl);
                    return new CachedToken(token, expiresAt);
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()) // Redis 블로킹 방지
                .onErrorResume(e -> {
                    log.warn("Redis 조회 실패 (무시됨): {}", e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> saveToken(CachedToken token) {
        return Mono.fromRunnable(() -> {
                    long remainingSeconds = token.remainingSeconds();
                    if (remainingSeconds > 0) {
                        redisTemplate.opsForValue().set(
                                TOKEN_KEY,
                                token.token(),
                                Duration.ofSeconds(remainingSeconds)
                        );
                    }
                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then()
                .onErrorResume(e -> {
                    log.error("Redis 저장 실패: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}
