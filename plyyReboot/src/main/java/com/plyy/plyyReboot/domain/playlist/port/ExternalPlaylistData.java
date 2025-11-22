package com.plyy.plyyReboot.domain.playlist.port;

import java.util.List;

/**
 * 외부 플레이리스트의 데이터를 담는 DTO
 */
public record ExternalPlaylistData(
        String id,
        String name,
        String description,
        String thumbnailUrl,
        List<ExternalTrackData> tracks
) {
    public record ExternalTrackData(
            String externalId,
            String title,
            String artist,
            String album,
            String albumArtUrl,
            int durationMs
    ) {}
}
