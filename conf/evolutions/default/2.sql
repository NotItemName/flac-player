# Artist schema

# --- !Ups

CREATE TABLE `ARTIST` (
  `id`   INT          NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


# --- !Downs

DROP TABLE IF EXISTS `ARTIST`;
