package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class InvalidPlaylistIdException extends BasePlaylistException {

    private final String value;

    public InvalidPlaylistIdException(String value) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_ID);
        this.value = value;
    }

    @Override
    public String getDetailInfo() {
        return String.format("[Value=%s]", value);
    }
}