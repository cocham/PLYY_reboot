package com.plyy.plyyReboot.domain.playlist;

import com.plyy.plyyReboot.domain.preference.tag.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Entity
@Table(name = "playlist_tag", uniqueConstraints = {
        @UniqueConstraint(name = "UK_playlisttag_playlist_tag", columnNames = {"playlist_id", "tag_id"})
})
@Getter
@NoArgsConstructor
public class PlaylistTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlayList playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public PlaylistTag(PlayList playlist, Tag tag) {
        this.playlist = playlist;
        this.tag = tag;
    }

    // (equals, hashCode)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistTag that = (PlaylistTag) o;
        return Objects.equals(playlist.getId(), that.playlist.getId()) &&
                Objects.equals(tag.getId(), that.tag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist.getId(), tag.getId());
    }
}
