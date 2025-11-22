package com.plyy.plyyReboot.domain.playlist;

import java.util.Objects;

/**
 * 플레이리스트 ID를 표현하는 Value Object
 * - 외부 플랫폼(Spotify, YouTube)의 플레이리스트 ID를 타입 안전하게 관리
 */
public class PlaylistId {
    private final String value;
    private final PlaylistSource source;

    private PlaylistId(String value, PlaylistSource source) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("플레이리스트 ID는 비어있을 수 없습니다.");
        }
        this.value = value;
        this.source = source;
    }

    public static PlaylistId fromUrl(String url, PlaylistSource source) {
        String extractedId = source.extractIdFromUrl(url);
        return new PlaylistId(extractedId, source);
    }

    public static PlaylistId of(String id, PlaylistSource source) {
        return new PlaylistId(id, source);
    }

    public String getValue() {
        return value;
    }

    public PlaylistSource getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistId that = (PlaylistId) o;
        return Objects.equals(value, that.value) && source == that.source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, source);
    }

    @Override
    public String toString() {
        return source.name() + ":" + value;
    }
}
