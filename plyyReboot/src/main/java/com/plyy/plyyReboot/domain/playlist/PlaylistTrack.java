package com.plyy.plyyReboot.domain.playlist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlist_track")
@Getter
@Setter
@NoArgsConstructor
public class PlaylistTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlayList playlist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(length = 500)
    private String trackIntroduction;

    @Column(nullable = false)
    private Integer trackOrder;

    // === 비즈니스 로직 ===

    /**
     * PlaylistTrack 생성 (팩토리 메서드)
     */
    public static PlaylistTrack of(Track track, int order, String introduction) {
        PlaylistTrack playlistTrack = new PlaylistTrack();
        playlistTrack.setTrack(track);
        playlistTrack.setTrackOrder(order);
        playlistTrack.setTrackIntroduction(introduction);
        return playlistTrack;
    }

    /**
     * 플레이리스트에 할당 (양방향 관계)
     */
    void assignToPlaylist(PlayList playlist) {
        this.playlist = playlist;
    }

    public void setTrack(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("트랙은 null일 수 없습니다.");
        }
        this.track = track;
    }

    public void setTrackOrder(int order) {
        if (order < 1) {
            throw new IllegalArgumentException("트랙 순서는 1 이상이어야 합니다.");
        }
        this.trackOrder = order;
    }

    public void setTrackIntroduction(String introduction) {
        if (introduction != null && introduction.length() > 500) {
            throw new IllegalArgumentException("트랙 소개는 500자를 초과할 수 없습니다.");
        }
        this.trackIntroduction = introduction;
    }

    /**
     * 큐레이션 여부 확인
     */
    public boolean isCurated() {
        return trackIntroduction != null && !trackIntroduction.isBlank();
    }

    /**
     * 순서 변경
     */
    public void changeOrder(int newOrder) {
        setTrackOrder(newOrder);
    }

    /**
     * 큐레이션 수정
     */
    public void updateCuration(String newIntroduction) {
        setTrackIntroduction(newIntroduction);
    }
}
