package com.plyy.plyyReboot.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 계층형 캐시 (L1 + L2)
 * - Read-through, Write-through 패턴
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TieredTokenCache implements TokenCacheStrategy<CachedToken> {

    private final InMemoryTokenCache l1Cache;
    private final RedisTokenCache l2Cache;

    @Override
    public Optional<CachedToken> get(String key) {
        // L1 캐시 확인
        Optional<CachedToken> l1Result = l1Cache.get(key);
        if (l1Result.isPresent()) {
            log.debug("L1 캐시 히트: key={}", key);
            return l1Result;
        }

        // L2 캐시 확인
        Optional<CachedToken> l2Result = l2Cache.get(key);
        if (l2Result.isPresent()) {
            log.debug("L2 캐시 히트: key={}", key);
            l1Cache.put(key, l2Result.get());
            return l2Result;
        }

        log.debug("캐시 미스: key={}", key);
        return Optional.empty();
    }

    @Override
    public void put(String key, CachedToken value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
        log.debug("계층형 캐시 저장 완료: key={}", key);
    }

    @Override
    public void evict(String key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
        log.debug("계층형 캐시 삭제 완료: key={}", key);
    }

    @Override
    public boolean contains(String key) {
        return l1Cache.contains(key) || l2Cache.contains(key);
    }
}
