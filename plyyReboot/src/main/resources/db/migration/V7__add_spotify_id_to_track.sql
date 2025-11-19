ALTER TABLE `track`
    ADD COLUMN `spotify_id` VARCHAR(255) NULL COMMENT '스포티파이 트랙 ID',
    ADD UNIQUE KEY `UK_track_spotify_id` (`spotify_id`);