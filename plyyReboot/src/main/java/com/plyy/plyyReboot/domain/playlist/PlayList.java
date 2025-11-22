package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.common.BaseTimeEntity;
import com.plyy.plyyReboot.domain.playlist.exception.InvalidPlaylistTitleException;
import com.plyy.plyyReboot.domain.preference.genre.Genre;
import com.plyy.plyyReboot.domain.preference.mood.Mood;
import com.plyy.plyyReboot.domain.preference.genre.SubGenre;
import com.plyy.plyyReboot.domain.preference.tag.Tag;
import com.plyy.plyyReboot.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
public class PlayList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curator_id", nullable = false)
    private User curator;

    private Integer totalTrackCount = 0;
    private Long totalDurationMs = 0L;

    @Column(length = 1000)
    private String introduction;
    private String thumbnailUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private Double avgBpm = 0.0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "master_genre_id")
    private Genre masterGenre;

    // --- 연관관계 매핑 ---

    // 플레이리스트 트랙 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("trackOrder ASC") // 트랙 순서 정렬
    private List<PlaylistTrack> tracks = new ArrayList<>();

    // 서브 장르 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistSubGenre> subGenres = new HashSet<>();

    // 무드 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistMood> moods = new HashSet<>();

    // 태그 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistTag> tags = new HashSet<>();

    // --- 편의 메서드 ---
    @Builder
    public PlayList(String title, User curator, Genre masterGenre, String introduction, String thumbnailUrl, String spotifyUrl) {
        validateTitle(title);
        if (curator == null) throw new IllegalArgumentException("큐레이터는 필수입니다.");
        if (masterGenre == null) throw new IllegalArgumentException("마스터 장르는 필수입니다.");

        this.title = title;
        this.curator = curator;
        this.masterGenre = masterGenre;
        this.introduction = introduction;
        this.thumbnailUrl = thumbnailUrl;
        this.spotifyUrl = spotifyUrl;
    }

    public void addTrack(Track track, int order, String trackIntroduction) {
        PlaylistTrack playlistTrack = PlaylistTrack.of(track, order, trackIntroduction);

        playlistTrack.assignToPlaylist(this);
        this.tracks.add(playlistTrack);

        this.totalTrackCount++;
        this.totalDurationMs += track.getDurationMs();
    }

    public void addTrack(PlaylistTrack playlistTrack) {
        this.tracks.add(playlistTrack);
        playlistTrack.setPlaylist(this);
    }

    public void addSubGenre(SubGenre subGenre) {
        PlaylistSubGenre playlistSubGenre = new PlaylistSubGenre(this, subGenre);
        this.subGenres.add(playlistSubGenre);
    }

    public void addMood(Mood mood) {
        PlaylistMood playlistMood = new PlaylistMood(this, mood);
        this.moods.add(playlistMood);
    }

    public void addTag(Tag tag) {
        PlaylistTag playlistTag = new PlaylistTag(this, tag);
        this.tags.add(playlistTag);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidPlaylistTitleException(title, "제목은 필수입니다.");
        }
    }
}
