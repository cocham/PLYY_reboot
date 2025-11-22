package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class UnsupportedPlaylistSourceException extends BasePlaylistException {

    private final String source;

    public UnsupportedPlaylistSourceException(String source) {
        super(PlaylistErrorCode.UNSUPPORTED_PLAYLIST_SOURCE);
        this.source = source;
    }

    @Override
    public String getDetailInfo() {
        return String.format("[Source=%s]", source);
    }
}