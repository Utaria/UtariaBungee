CREATE TABLE `bans` (
  `id`           INT(11)      NOT NULL AUTO_INCREMENT,
  `player`       VARCHAR(60)  NULL     DEFAULT NULL COLLATE 'utf8_bin',
  `ip`           VARCHAR(80)  NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `reason`       VARCHAR(255) NOT NULL
  COLLATE 'utf8_unicode_ci',
  `server`       VARCHAR(255) NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `banned_by`    VARCHAR(80)  NOT NULL
  COLLATE 'utf8_unicode_ci',
  `date`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ban_end`      TIMESTAMP    NULL     DEFAULT NULL,
  `unbanned_by`  VARCHAR(80)  NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `unban_date`   TIMESTAMP    NULL     DEFAULT NULL,
  `unban_reason` VARCHAR(255) NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  PRIMARY KEY (`id`)
);

CREATE TABLE `kicks` (
  `id`        INT(11)      NOT NULL AUTO_INCREMENT,
  `player`    VARCHAR(60)  NULL     DEFAULT NULL COLLATE 'utf8_bin',
  `ip`        VARCHAR(80)  NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `reason`    VARCHAR(255) NOT NULL
  COLLATE 'utf8_unicode_ci',
  `server`    VARCHAR(255) NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `kicked_by` VARCHAR(80)  NOT NULL
  COLLATE 'utf8_unicode_ci',
  `date`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE `mutes` (
  `id`            INT(11)      NOT NULL AUTO_INCREMENT,
  `player`        VARCHAR(60)  NULL     DEFAULT NULL COLLATE 'utf8_bin',
  `ip`            VARCHAR(80)  NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `reason`        VARCHAR(255) NOT NULL
  COLLATE 'utf8_unicode_ci',
  `server`        VARCHAR(255) NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `muted_by`      VARCHAR(80)  NOT NULL
  COLLATE 'utf8_unicode_ci',
  `date`          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mute_end`      TIMESTAMP    NOT NULL DEFAULT '0000-00-00 00:00:00',
  `unmuted_by`    VARCHAR(80)  NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  `unmute_date`   TIMESTAMP    NULL     DEFAULT NULL,
  `unmute_reason` VARCHAR(255) NULL     DEFAULT NULL COLLATE 'utf8_unicode_ci',
  PRIMARY KEY (`id`)
);

