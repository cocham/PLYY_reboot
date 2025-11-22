package com.plyy.plyyReboot.infrastructure.cache;

import java.util.Optional;

/**
 * 토큰 캐시 전략 인터페이스
 * - 다양한 캐시 구현체를 지원하는 전략 패턴
 */
public interface TokenCacheStrategy<T> {
    Optional<T> get(String key);
    void put(String key, T value);
    void evict(String key);
    boolean contains(String key);
}

