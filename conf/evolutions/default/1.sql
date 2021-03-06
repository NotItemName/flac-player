# Flac-Player schema

# --- !Ups
CREATE TABLE `GENRE` (
  `id`   INT          NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `ARTIST` (
  `id`   INT          NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `ALBUM` (
  `id`       INT          NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(255) NOT NULL,
  `year`     INT          NOT NULL,
  `artistId` INT          NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`artistId`)
  REFERENCES `ARTIST` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  UNIQUE KEY `album_artist_idx`(`name`, `artistId`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `SONG` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255) NOT NULL,
  `trackNumber` INT          NOT NULL,
  `fileName`    VARCHAR(255) NOT NULL UNIQUE,
  `artistId`    INT          NOT NULL,
  `albumId`     INT          NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`artistId`)
  REFERENCES `ARTIST` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY (`albumId`)
  REFERENCES `ALBUM` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  UNIQUE KEY `song_album_artist_idx`(`name`, `artistId`, `albumId`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `ALBUM_GENRE` (
  `albumId` INT NOT NULL,
  `genreId` INT NOT NULL,
  FOREIGN KEY (`genreId`)
  REFERENCES `GENRE` (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE,
  FOREIGN KEY (`albumId`)
  REFERENCES `ALBUM` (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `SONG_GENRE` (
  `songId`  INT NOT NULL,
  `genreId` INT NOT NULL,
  FOREIGN KEY (`genreId`)
  REFERENCES `GENRE` (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE,
  FOREIGN KEY (songId)
  REFERENCES `SONG` (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



# --- !Downs
DROP TABLE IF EXISTS `SONG_GENRE`;
DROP TABLE IF EXISTS `ALBUM_GENRE`;
DROP TABLE IF EXISTS `SONG`;
DROP TABLE IF EXISTS `ALBUM`;
DROP TABLE IF EXISTS `ARTIST`;
DROP TABLE IF EXISTS `GENRE`;
