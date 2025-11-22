-- --------------------------------------------------------
-- 트랙에 대한 좋아요 기록 테이블 (track_like)
-- --------------------------------------------------------
CREATE TABLE `track_like` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `user_id` BIGINT NOT NULL,
                              `track_id` BIGINT NOT NULL,
                              `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                              PRIMARY KEY (`id`),
    -- 한 사용자가 같은 곡에 두 번 좋아요를 누르지 못하도록 유니크 제약 조건 설정
                              UNIQUE KEY `UK_tracklike_user_track` (`user_id`, `track_id`),
                              KEY `FK_tracklike_user` (`user_id`),
                              KEY `FK_tracklike_track` (`track_id`),
    -- 사용자 또는 트랙 삭제 시 좋아요 기록도 삭제 (ON DELETE CASCADE)
                              CONSTRAINT `FK_tracklike_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `FK_tracklike_track` FOREIGN KEY (`track_id`) REFERENCES `track` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;