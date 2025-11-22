package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class GenreNotFoundException extends BasePlaylistException {

    private final Long genreId;

    public GenreNotFoundException(Long genreId) {
        super(PlaylistErrorCode.GENRE_NOT_FOUND);
        this.genreId = genreId;
    }

    public GenreNotFoundException(String message) {
        super(PlaylistErrorCode.GENRE_NOT_FOUND, message);
        this.genreId = null;
    }

    public GenreNotFoundException(Long genreId, String message) {
        super(PlaylistErrorCode.GENRE_NOT_FOUND, message);
        this.genreId = genreId;
    }

    @Override
    public String getDetailInfo() {
        if (genreId != null) {
            return String.format("[GenreId=%d]", genreId);
        }
        return "[No ID Info]";
    }
}