package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class SubGenreNotFoundException extends BasePlaylistException {

    private final Long subGenreId;

    public SubGenreNotFoundException(Long subGenreId) {
        super(PlaylistErrorCode.SUB_GENRE_NOT_FOUND);
        this.subGenreId = subGenreId;
    }

    public SubGenreNotFoundException(String message) {
        super(PlaylistErrorCode.SUB_GENRE_NOT_FOUND, message);
        this.subGenreId = null;
    }

    public SubGenreNotFoundException(Long subGenreId, String message) {
        super(PlaylistErrorCode.SUB_GENRE_NOT_FOUND, message);
        this.subGenreId = subGenreId;
    }

    @Override
    public String getDetailInfo() {
        if (subGenreId != null) {
            return String.format("[SubGenreId=%d]", subGenreId);
        }
        return "[No ID Info]";
    }
}
