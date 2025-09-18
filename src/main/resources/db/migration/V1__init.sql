-- V1__init_schema.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) 회원
CREATE TABLE IF NOT EXISTS `member` (
                                        id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                        email      VARCHAR(255) NOT NULL UNIQUE,
                                        password   VARCHAR(100) NOT NULL,
                                        name       VARCHAR(50)  NOT NULL,
                                        created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        deleted    TINYINT      NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) 공동구매 그룹 (예약어이므로 항상 백틱)
CREATE TABLE IF NOT EXISTS `group` (
                                       id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       host_id      BIGINT UNSIGNED NOT NULL,
                                       title        VARCHAR(100) NOT NULL,
                                       description  TEXT,
                                       expires_at   DATETIME     NOT NULL,
                                       max_member   INT          NOT NULL,
                                       created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
                                       CONSTRAINT fk_pg_host FOREIGN KEY (host_id) REFERENCES `member`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_pg_host_id ON `group`(host_id);

-- 3) 참여
CREATE TABLE IF NOT EXISTS participation (
                                             id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                             group_id   BIGINT UNSIGNED NOT NULL,
                                             member_id  BIGINT UNSIGNED NOT NULL,
                                             joined_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             CONSTRAINT fk_part_group  FOREIGN KEY (group_id)  REFERENCES `group`(id),
                                             CONSTRAINT fk_part_member FOREIGN KEY (member_id) REFERENCES `member`(id),
                                             UNIQUE KEY uq_group_member (group_id, member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_part_group_id  ON participation(group_id);
CREATE INDEX idx_part_member_id ON participation(member_id);

-- 4) 그룹 게시글
CREATE TABLE IF NOT EXISTS post (
                                    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                    group_id   BIGINT UNSIGNED NOT NULL,
                                    host_id    BIGINT UNSIGNED NOT NULL,
                                    title      VARCHAR(200) NOT NULL,
                                    content    TEXT         NOT NULL,
                                    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_post_group FOREIGN KEY (group_id) REFERENCES `group`(id),
                                    CONSTRAINT fk_post_host  FOREIGN KEY (host_id)  REFERENCES `member`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_post_group_id ON post(group_id);
CREATE INDEX idx_post_host_id  ON post(host_id);

-- 5) 게시글 댓글
CREATE TABLE IF NOT EXISTS comment (
                                       id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       post_id    BIGINT UNSIGNED NOT NULL,
                                       member_id  BIGINT UNSIGNED NOT NULL,
                                       content    TEXT         NOT NULL,
                                       created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_comment_post   FOREIGN KEY (post_id)   REFERENCES post(id),
                                       CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES `member`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_comment_post_id   ON comment(post_id);
CREATE INDEX idx_comment_member_id ON comment(member_id);

SET FOREIGN_KEY_CHECKS = 1;
