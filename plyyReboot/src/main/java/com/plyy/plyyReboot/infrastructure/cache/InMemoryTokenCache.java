package com.plyy.plyyReboot.infrastructure.cache;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 메모리 캐시 전략 (L1)
 */
@Component
public class InMemoryTokenCache implements TokenCacheStrategy<CachedToken> {

    private final Map<String, CachedToken> cache = new ConcurrentHashMap<>();
    private static final int MAX_SIZE = 100;

    @Override
    public Optional<CachedToken> get(String key) {
        CachedToken token = cache.get(key);
        if (token != null && !token.isExpired()) {
            return Optional.of(token);
        }
        cache.remove(key);
        return Optional.empty();
    }

    @Override
    public void put(String key, CachedToken value) {
        if (cache.size() >= MAX_SIZE) {
            evictOldest();
        }
        cache.put(key, value);
    }

    @Override
    public void evict(String key) {
        cache.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return get(key).isPresent();
    }

    private void evictOldest() {
        cache.entrySet().stream()
                .min(Comparator.comparing(e -> e.getValue().expiresAt()))
                .ifPresent(e -> cache.remove(e.getKey()));
    }

    public void clear() {
        cache.clear();
    }
}
