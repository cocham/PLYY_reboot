package com.plyy.plyyReboot.domain.playlist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "track")
@Getter
@Setter
@NoArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String spotifyId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    private String album;
    private String albumArtUrl;
    private Integer durationMs;
    private Integer bpm;

    /**
     * Spotify 데이터로부터 Track 생성 (팩토리 메서드)
     */
    public static Track fromSpotify(String spotifyId,
                                    String title,
                                    String artist,
                                    String album,
                                    String albumArtUrl,
                                    int durationMs) {
        Track track = new Track();
        track.setSpotifyId(spotifyId);
        track.setTitle(title);
        track.setArtist(artist);
        track.setAlbum(album);
        track.setAlbumArtUrl(albumArtUrl);
        track.setDurationMs(durationMs);
        return track;
    }
}
