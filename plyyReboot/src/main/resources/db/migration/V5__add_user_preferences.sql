-- 3. 분류 및 태깅 (온보딩 DTO 저장을 위한 N:M 매핑 테이블 추가)

CREATE TABLE `user_genre` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `user_id` bigint NOT NULL,
                              `genre_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `UK_user_genre_user_genre` (`user_id`, `genre_id`),
                              KEY `FK_usergenre_user` (`user_id`),
                              KEY `FK_usergenre_genre` (`genre_id`),
                              CONSTRAINT `FK_usergenre_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `FK_usergenre_genre` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_sub_genre` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `user_id` bigint NOT NULL,
                                  `sub_genre_id` bigint NOT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UK_usersubgenre_user_subgenre` (`user_id`, `sub_genre_id`),
                                  KEY `FK_usersubgenre_user` (`user_id`),
                                  KEY `FK_usersubgenre_subgenre` (`sub_genre_id`),
                                  CONSTRAINT `FK_usersubgenre_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `FK_usersubgenre_subgenre` FOREIGN KEY (`sub_genre_id`) REFERENCES `sub_genre` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;