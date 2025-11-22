package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class InvalidPlaylistTitleException extends BasePlaylistException {

    private final String invalidTitle;

    public InvalidPlaylistTitleException(String invalidTitle) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_TITLE);
        this.invalidTitle = invalidTitle;
    }

    public InvalidPlaylistTitleException(String invalidTitle, String message) {
        super(PlaylistErrorCode.INVALID_PLAYLIST_TITLE, message);
        this.invalidTitle = invalidTitle;
    }

    @Override
    public String getDetailInfo() {
        if (invalidTitle != null) {
            return String.format("[Input Title Length: %d]", invalidTitle.length());
        }
        return String.format("[Input Title Length: %d]", 0);
    }
}
