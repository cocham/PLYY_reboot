package com.plyy.plyyReboot.domain.playlist.exception;

public class PlaylistFetchFailureException extends BasePlaylistException {
    private final String playlistId;

    public PlaylistFetchFailureException(String playlistId, Throwable cause) {
        super(PlaylistErrorCode.PLAYLIST_FETCH_FAILED);
        this.initCause(cause);
        this.playlistId = playlistId;
    }

    public PlaylistFetchFailureException(String playlistId, String debugMessage) {
        super(PlaylistErrorCode.PLAYLIST_FETCH_FAILED, debugMessage);
        this.playlistId = playlistId;
    }

    @Override
    public String getDetailInfo() {
        return String.format("[PlaylistID=%s]", playlistId);
    }
}
