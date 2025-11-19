package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.preference.SubGenre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Entity
@Table(name = "playlist_sub_genre", uniqueConstraints = {
        @UniqueConstraint(name = "UK_playlist_sub_genre", columnNames = {"playlist_id", "sub_genre_id"})
})
@Getter
@NoArgsConstructor
public class PlaylistSubGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlayList playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_genre_id", nullable = false)
    private SubGenre subGenre;

    public PlaylistSubGenre(PlayList playlist, SubGenre subGenre) {
        this.playlist = playlist;
        this.subGenre = subGenre;
    }

    // (equals, hashCode - Set 사용을 위해)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistSubGenre that = (PlaylistSubGenre) o;
        return Objects.equals(playlist.getId(), that.playlist.getId()) &&
                Objects.equals(subGenre.getId(), that.subGenre.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist.getId(), subGenre.getId());
    }
}