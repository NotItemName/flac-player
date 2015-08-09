# Song and album schemas

# --- !Ups
CREATE TABLE `ALBUM` (
  `id`       INT          NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(255) NOT NULL,
  `year`     INT          NOT NULL,
  `artistId` INT          NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (artistId)
  REFERENCES `ARTIST` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `SONG` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255) NOT NULL,
  `trackNumber` VARCHAR(255) NOT NULL,
  `fileName`    VARCHAR(255) NOT NULL UNIQUE,
  `artistId`    INT          NOT NULL,
  `albumId`     INT          NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (artistId)
  REFERENCES `ARTIST` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY (albumId)
  REFERENCES `ALBUM` (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


# --- !Downs
DROP TABLE IF EXISTS `ALBUM`;
DROP TABLE IF EXISTS `SONG`;
