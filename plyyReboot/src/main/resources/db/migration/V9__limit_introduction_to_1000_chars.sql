-- playlist.introduction / playlist_track.track_introduction 을 VARCHAR(1000)으로 제한


-- 1) playlist.introduction: TEXT -> VARCHAR(1000)
ALTER TABLE playlist
    MODIFY COLUMN introduction VARCHAR(1000) DEFAULT NULL;

-- 2) playlist_track.track_introduction: TEXT -> VARCHAR(1000)
ALTER TABLE playlist_track
    MODIFY COLUMN track_introduction VARCHAR(1000) DEFAULT NULL;
