-- 1. playlist 테이블에 master_genre_id 컬럼 추가 (NULL 허용)
ALTER TABLE `playlist`
    ADD COLUMN `master_genre_id` BIGINT NULL COMMENT '마스터 장르 ID' AFTER `spotify_url`,
    ADD CONSTRAINT `FK_playlist_master_genre`
        FOREIGN KEY (`master_genre_id`)
        REFERENCES `genre` (`id`)
        ON DELETE SET NULL; -- >장르가 삭제되어도 플레이리스트 유지

-- 2. 플레이리스트와 서브 장르 간의 N:M 매핑 테이블
CREATE TABLE `playlist_sub_genre` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `playlist_id` BIGINT NOT NULL,
                                      `sub_genre_id` BIGINT NOT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `UK_playlist_sub_genre` (`playlist_id`, `sub_genre_id`),
                                      CONSTRAINT `FK_playlistsubgenre_playlist`
                                          FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
                                      CONSTRAINT `FK_playlistsubgenre_subgenre`
                                          FOREIGN KEY (`sub_genre_id`) REFERENCES `sub_genre` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;