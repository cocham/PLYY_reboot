package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.preference.mood.Mood;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Entity
@Table(name = "playlist_mood", uniqueConstraints = {
        @UniqueConstraint(name = "UK_playlistmood_playlist_mood", columnNames = {"playlist_id", "mood_id"})
})
@Getter
@NoArgsConstructor
public class PlaylistMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlayList playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood;

    public PlaylistMood(PlayList playlist, Mood mood) {
        this.playlist = playlist;
        this.mood = mood;
    }

    // (equals, hashCode)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistMood that = (PlaylistMood) o;
        return Objects.equals(playlist.getId(), that.playlist.getId()) &&
                Objects.equals(mood.getId(), that.mood.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist.getId(), mood.getId());
    }
}