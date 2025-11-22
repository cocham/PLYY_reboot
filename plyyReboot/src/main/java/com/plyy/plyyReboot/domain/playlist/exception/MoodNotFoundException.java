package com.plyy.plyyReboot.domain.playlist.exception;

import lombok.Getter;

@Getter
public class MoodNotFoundException extends BasePlaylistException {

    private final Long moodId;

    public MoodNotFoundException(Long moodId) {
        super(PlaylistErrorCode.MOOD_NOT_FOUND);
        this.moodId = moodId;
    }

    public MoodNotFoundException(String message) {
        super(PlaylistErrorCode.MOOD_NOT_FOUND, message);
        this.moodId = null;
    }

    public MoodNotFoundException(Long moodId, String message) {
        super(PlaylistErrorCode.MOOD_NOT_FOUND, message);
        this.moodId = moodId;
    }

    @Override
    public String getDetailInfo() {
        if (moodId != null) {
            return String.format("[MoodId=%d]", moodId);
        }
        return "[No ID Info]";
    }
}
