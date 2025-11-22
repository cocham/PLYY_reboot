package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class InvalidPlaylistUrlException extends BasePlaylistException {

    private final String url;

    public InvalidPlaylistUrlException(String url) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_URL);
        this.url = url;
    }

    public InvalidPlaylistUrlException(String url, Throwable cause) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_URL, PlaylistErrorCode.INVALID_PLAYLIST_URL.getMessage());        // 또는 cause를 받는 생성자가 Base에 없다면 initCause 사용
        this.initCause(cause);
        this.url = url;
    }

    public InvalidPlaylistUrlException(String url, String message) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_URL, message);
        this.url = url;
    }

    @Override
    public String getDetailInfo() {
        return String.format("[URL=%s]", url);
    }
}