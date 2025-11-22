package com.plyy.plyyReboot.infrastructure.external.spotify;

import com.plyy.plyyReboot.infrastructure.cache.CachedToken;
import reactor.core.publisher.Mono;

public interface SpotifyTokenRepository {
    Mono<CachedToken> findToken();
    Mono<Void> saveToken(CachedToken token);
}
