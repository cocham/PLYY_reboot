-- 1. 유저 및 소셜 기능
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `email` varchar(255) NOT NULL,
                        `nickname` varchar(255) NOT NULL,
                        `role` varchar(20) DEFAULT NULL,
                        `provider` varchar(20) DEFAULT NULL,
                        `social_id` varchar(255) DEFAULT NULL,
                        `thumbnail_url` varchar(500) DEFAULT NULL,
                        `introduction` varchar(1000) DEFAULT NULL,
                        `playlist_count` int DEFAULT 0,
                        `follower_count` int DEFAULT 0,
                        `total_like_count` int DEFAULT 0,
                        `total_bookmark_count` int DEFAULT 0,
                        `fcm_token` varchar(255) DEFAULT NULL,
                        `is_news_opted_in` tinyint(1) DEFAULT 0,
                        `last_login_at` datetime(6) DEFAULT NULL,
                        `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                        `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        `withdrawn_at` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_user_email` (`email`),
                        UNIQUE KEY `UK_user_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `track` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `title` varchar(255) NOT NULL,
                         `artist` varchar(255) NOT NULL,
                         `album` varchar(255) DEFAULT NULL,
                         `album_art_url` varchar(500) DEFAULT NULL,
                         `duration_ms` int DEFAULT NULL,
                         `bpm` int DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `title` varchar(255) NOT NULL,
                            `curator_id` bigint NOT NULL,
                            `total_track_count` int DEFAULT 0,
                            `total_duration_ms` bigint DEFAULT 0,
                            `introduction` text DEFAULT NULL,
                            `thumbnail_url` varchar(500) DEFAULT NULL,
                            `youtube_url` varchar(500) DEFAULT NULL,
                            `spotify_url` varchar(500) DEFAULT NULL,
                            `avg_bpm` double DEFAULT 0,
                            `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                            `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                            PRIMARY KEY (`id`),
                            KEY `FK_playlist_curator` (`curator_id`),
                            CONSTRAINT `FK_playlist_curator` FOREIGN KEY (`curator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist_track` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `playlist_id` bigint NOT NULL,
                                  `track_id` bigint NOT NULL,
                                  `track_introduction` text DEFAULT NULL,
                                  `track_order` int DEFAULT NULL,
                                  `video_url` varchar(500) DEFAULT NULL,
                                  `spotify_url` varchar(500) DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `FK_playlisttrack_playlist` (`playlist_id`),
                                  KEY `FK_playlisttrack_track` (`track_id`),
                                  CONSTRAINT `FK_playlisttrack_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `FK_playlisttrack_track` FOREIGN KEY (`track_id`) REFERENCES `track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist_like` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `user_id` bigint NOT NULL,
                                 `playlist_id` bigint NOT NULL,
                                 `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `UK_playlistlike_user_playlist` (`user_id`, `playlist_id`),
                                 KEY `FK_playlistlike_user` (`user_id`),
                                 KEY `FK_playlistlike_playlist` (`playlist_id`),
                                 CONSTRAINT `FK_playlistlike_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `FK_playlistlike_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist_bookmark` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `user_id` bigint NOT NULL,
                                     `playlist_id` bigint NOT NULL,
                                     `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `UK_playlistbookmark_user_playlist` (`user_id`, `playlist_id`),
                                     KEY `FK_playlistbookmark_user` (`user_id`),
                                     KEY `FK_playlistbookmark_playlist` (`playlist_id`),
                                     CONSTRAINT `FK_playlistbookmark_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                     CONSTRAINT `FK_playlistbookmark_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `curator_follower` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `follower_id` bigint NOT NULL,
                                    `curator_id` bigint NOT NULL,
                                    `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `UK_curatorfollower_follower_curator` (`follower_id`, `curator_id`),
                                    KEY `FK_curatorfollower_follower` (`follower_id`),
                                    KEY `FK_curatorfollower_curator` (`curator_id`),
                                    CONSTRAINT `FK_curatorfollower_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `FK_curatorfollower_curator` FOREIGN KEY (`curator_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 커뮤니티
CREATE TABLE `support_message` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `author_id` bigint NOT NULL,
                                   `curator_id` bigint NOT NULL,
                                   `content` text NOT NULL,
                                   `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                   `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                   `deleted_at` datetime(6) DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `FK_supportmessage_author` (`author_id`),
                                   KEY `FK_supportmessage_curator` (`curator_id`),
                                   CONSTRAINT `FK_supportmessage_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
                                   CONSTRAINT `FK_supportmessage_curator` FOREIGN KEY (`curator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `support_message_like` (
                                        `id` bigint NOT NULL AUTO_INCREMENT,
                                        `user_id` bigint NOT NULL,
                                        `support_message_id` bigint NOT NULL,
                                        `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `UK_supportmessagelike_user_message` (`user_id`, `support_message_id`),
                                        KEY `FK_supportmessagelike_user` (`user_id`),
                                        KEY `FK_supportmessagelike_message` (`support_message_id`),
                                        CONSTRAINT `FK_supportmessagelike_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                        CONSTRAINT `FK_supportmessagelike_message` FOREIGN KEY (`support_message_id`) REFERENCES `support_message` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comment` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `author_id` bigint NOT NULL,
                           `content` text NOT NULL,
                           `commentable_type` varchar(50) NOT NULL,
                           `commentable_id` bigint NOT NULL,
                           `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                           `updated_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                           `deleted_at` datetime(6) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FK_comment_author` (`author_id`),
                           CONSTRAINT `FK_comment_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 분류 및 태깅
CREATE TABLE `tag` (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `name` varchar(255) NOT NULL,
                       `type` varchar(50) NOT NULL,
                       PRIMARY KEY (`id`),
                       UNIQUE KEY `UK_tag_name_type` (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `curator_tag` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint NOT NULL,
                               `tag_id` bigint NOT NULL,
                               `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `UK_curatortag_user_tag` (`user_id`, `tag_id`),
                               KEY `FK_curatortag_user` (`user_id`),
                               KEY `FK_curatortag_tag` (`tag_id`),
                               CONSTRAINT `FK_curatortag_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_curatortag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist_tag` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `playlist_id` bigint NOT NULL,
                                `tag_id` bigint NOT NULL,
                                `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `UK_playlisttag_playlist_tag` (`playlist_id`, `tag_id`),
                                KEY `FK_playlisttag_playlist` (`playlist_id`),
                                KEY `FK_playlisttag_tag` (`tag_id`),
                                CONSTRAINT `FK_playlisttag_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `FK_playlisttag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `genre` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(100) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK_genre_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sub_genre` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `parent_genre_id` bigint NOT NULL,
                             `name` varchar(100) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK_subgenre_genre` (`parent_genre_id`),
                             CONSTRAINT `FK_subgenre_genre` FOREIGN KEY (`parent_genre_id`) REFERENCES `genre` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `mood` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `name` varchar(100) NOT NULL,
                        `category` varchar(100) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_mood_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_mood` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `user_id` bigint NOT NULL,
                             `mood_id` bigint NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UK_usermood_user_mood` (`user_id`, `mood_id`),
                             KEY `FK_usermood_user` (`user_id`),
                             KEY `FK_usermood_mood` (`mood_id`),
                             CONSTRAINT `FK_usermood_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                             CONSTRAINT `FK_usermood_mood` FOREIGN KEY (`mood_id`) REFERENCES `mood` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `playlist_mood` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `playlist_id` bigint NOT NULL,
                                 `mood_id` bigint NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `UK_playlistmood_playlist_mood` (`playlist_id`, `mood_id`),
                                 KEY `FK_playlistmood_playlist` (`playlist_id`),
                                 KEY `FK_playlistmood_mood` (`mood_id`),
                                 CONSTRAINT `FK_playlistmood_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `FK_playlistmood_mood` FOREIGN KEY (`mood_id`) REFERENCES `mood` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 통계 및 알림
CREATE TABLE `playlist_view_log` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `playlist_id` bigint NOT NULL,
                                     `user_id` bigint DEFAULT NULL,
                                     `inflow_type` varchar(50) DEFAULT NULL,
                                     `inflow_value` varchar(100) DEFAULT NULL,
                                     `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                     PRIMARY KEY (`id`),
                                     KEY `FK_playlistviewlog_playlist` (`playlist_id`),
                                     KEY `FK_playlistviewlog_user` (`user_id`),
                                     CONSTRAINT `FK_playlistviewlog_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE,
                                     CONSTRAINT `FK_playlistviewlog_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `search_log` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `keyword` varchar(100) NOT NULL,
                              `type` varchar(50) DEFAULT NULL,
                              `user_id` bigint DEFAULT NULL,
                              `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                              PRIMARY KEY (`id`),
                              KEY `FK_searchlog_user` (`user_id`),
                              CONSTRAINT `FK_searchlog_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notification` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `user_id` bigint NOT NULL,
                                `type` varchar(50) NOT NULL,
                                `is_read` tinyint(1) DEFAULT 0,
                                `actor_id` bigint NOT NULL,
                                `linkable_type` varchar(50) DEFAULT NULL,
                                `linkable_id` bigint DEFAULT NULL,
                                `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                                PRIMARY KEY (`id`),
                                KEY `FK_notification_user` (`user_id`),
                                KEY `FK_notification_actor` (`actor_id`),
                                CONSTRAINT `FK_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `FK_notification_actor` FOREIGN KEY (`actor_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

