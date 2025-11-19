package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.preference.Genre;
import com.plyy.plyyReboot.domain.preference.Mood;
import com.plyy.plyyReboot.domain.preference.SubGenre;
import com.plyy.plyyReboot.domain.preference.Tag;
import com.plyy.plyyReboot.domain.user.User;
import jakarta.persistence.*;
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
public class PlayList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curator_id", nullable = false)
    private User curator;

    private Integer totalTrackCount = 0;
    private Long totalDurationMs = 0L;
    private String introduction;
    private String thumbnailUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private Double avgBpm = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_genre_id")
    private Genre masterGenre;

    // --- 연관관계 매핑 ---

    // (양방향) 플레이리스트 트랙 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("trackOrder ASC") // 트랙 순서 정렬
    private List<PlaylistTrack> tracks = new ArrayList<>();

    // (양방향) 서브 장르 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistSubGenre> subGenres = new HashSet<>();

    // (양방향) 무드 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistMood> moods = new HashSet<>();

    // (양방향) 태그 (N:M)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistTag> tags = new HashSet<>();

    // --- 편의 메서드 ---
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
}