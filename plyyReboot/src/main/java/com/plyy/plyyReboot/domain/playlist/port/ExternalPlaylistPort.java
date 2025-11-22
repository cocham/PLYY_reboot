package com.plyy.plyyReboot.domain.playlist.port;

import com.plyy.plyyReboot.domain.playlist.PlaylistId;
import com.plyy.plyyReboot.domain.playlist.PlaylistSource;
import reactor.core.publisher.Mono;

/**
 * 외부 플레이리스트 조회를 위한 포트 인터페이스 (헥사고날 아키텍처)
 * - 도메인이 외부 시스템(Spotify, YouTube)에 의존하지 않도록 추상화
 * - 구현체는 인프라 레이어에 위치
 */
public interface ExternalPlaylistPort {
    Mono<ExternalPlaylistData> fetchPlaylist(PlaylistId playlistId);
    PlaylistSource getSupportedSource();
}
